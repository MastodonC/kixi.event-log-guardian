(ns kixi.event-log-guardian
  (:gen-class)
  (:require [aero.core :as aero]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [kixi.event-log-guardian.model :as model]
            [kixi.event-log-guardian.s3 :as s3]
            [kixi.event-log-guardian.cloudwatch :as cloudwatch]
            [kixi.log :as kixi-log]
            [taoensso.timbre :as log]))

(defn validate-config
  [config]
  (if (s/valid? ::model/config config)
    [true (s/unform ::model/config (s/conform ::model/config config))]
    [false (s/explain-data ::model/config config)]))

(defn configure-logging
  [config]
  (let [level-config {:level (get-in config [:logging :level])
                      :ns-blacklist (get-in config [:logging :ns-blacklist])
                      :timestamp-opts kixi-log/default-timestamp-opts ; iso8601 timestamps
                      :appenders (case (get-in config [:logging :appender])
                                   :println {:println (log/println-appender)}
                                   :json {:direct-json (kixi-log/timbre-appender-logstash)})}]
    (log/set-config! level-config)
    (log/handle-uncaught-jvm-exceptions!
     (fn [throwable ^Thread thread]
       (log/fatal throwable (str "Unhandled exception on " (.getName thread)))))))


(defn execute
  [config]
  (configure-logging config)
  (log/debug (str "Configuration: " config))
  (doseq [x (range 1 (:days-in-the-past config))]
    (let [s3-count (s3/get-event-count-for-day config (t/minus (t/today) (t/days x)))
          cw-count (cloudwatch/get-event-count-for-day config (t/minus (t/today) (t/days x)))
          data {:cloudwatch-count cw-count
                :s3-count         s3-count
                :date             (tf/unparse (tf/formatters :basic-date-time) (t/minus (t/today) (t/days x)))}]
      (if (= s3-count cw-count)
        (log/infof "Event count comparison: success")
        (log/errorf "Event count comparison: failure"))
      (log/infof "Event count comparison: %s" (str data)))))

(defn exit
  [status msg]
  (clojure.pprint/pprint msg)
  (System/exit status))

(defn -main
  [& args]
  (let [[ok? config] ((comp validate-config aero/read-config io/resource) "config.edn")]
    (if ok?
      (exit 0 (execute config))
      (exit 1 config))))
