(ns kixi.event-log-guardian.s3
  (:require [amazonica.aws.s3 :as s3]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clojure.spec.alpha :as s]
            [kixi.event-log-guardian.model :as model]
            [kixi.event-log-guardian.hour->s3-object-summaries :refer [hour->s3-object-summaries]]
            [kixi.event-log-guardian.s3-object-summary->event-count :refer [s3-object-summary->event-count]]))

(def one-hour (t/hours 1))

(defn day->hours
  [day]
  (p/periodic-seq (t/with-time-at-start-of-day day)
                  (t/with-time-at-start-of-day (t/plus day (t/days 1)))
                  one-hour))

(s/fdef get-event-count-for-day
        :args (s/cat :config ::model/config
                     :day model/time?)
        :ret int?)

(defn get-event-count-for-day
  [config day]
  (transduce
   (comp (mapcat (partial hour->s3-object-summaries config))
         (map (partial s3-object-summary->event-count config)))
   +
   (day->hours day)))
