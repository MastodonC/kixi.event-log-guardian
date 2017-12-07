(ns kixi.event-log-guardian
  (:gen-class)
  (:require [aero.core :as aero]
            [clj-time.core :as t]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [kixi.event-log-guardian.model :as model]
            [kixi.event-log-guardian.s3 :as s3]
            [kixi.event-log-guardian.cloudwatch :as cloudwatch]))

(defn validate-config
  [config]
  (if (s/valid? ::model/config config)
    [true (s/unform ::model/config (s/conform ::model/config config))]
    [false (s/explain-data ::model/config config)]))

(defn execute
  [config]
  (println "Configuration: ")
  (pprint config)
  (let [s3-count (s3/get-event-count-for-day (t/yesterday))
        cw-count (cloudwatch/get-event-count-for-day (t/yesterday))]
    (when-not (= s3-count cw-count)
      (throw (new Exception "EXPLOSION")))))

(defn exit
  [status msg]
  (println msg)
  (System/exit status))

(defn -main
  [& args]
  (let [[ok? config] ((comp validate-config aero/read-config io/resource) "config.edn")]
    (if ok?
      (exit 0 (execute config))
      (exit 1 config))))
