(ns kixi.event-log-guardian.cloudwatch
  (:require [amazonica.aws.cloudwatch :as cw]
            [clj-time.core :as t]
            [clj-time.coerce :as ct]
            [clojure.spec.alpha :as s]
            [kixi.event-log-guardian.model :as model]))

(def day-seconds 86400)

(s/fdef get-event-count-for-day
        :args (s/cat :config ::model/config
                     :day model/time?)
        :ret int?)

(defn get-event-count-for-day
  [{:keys [region
           stream-name]
    :as config}
   day]
  (->
   (cw/get-metric-statistics {:endpoint region}
                             {:namespace "AWS/Kinesis"
                              :metric-name "IncomingRecords"
                              :statistics ["Sum"]
                              :start-time (ct/to-date (t/with-time-at-start-of-day day))
                              :end-time (ct/to-date (t/with-time-at-start-of-day (t/plus day (t/days 1))))
                              :period day-seconds
                              :dimensions [{:name "StreamName" :value stream-name}]})
   :datapoints
   first
   :sum
   int))
