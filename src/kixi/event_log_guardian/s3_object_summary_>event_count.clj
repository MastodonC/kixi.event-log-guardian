(ns kixi.event-log-guardian.s3-object-summary->event-count
  (:require [amazonica.aws.s3 :as s3]
            [baldr.core :as baldr]
            [clojure.spec.alpha :as s]
            [kixi.event-log-guardian.model :as model])
  (:import [java.io InputStream]))

(s/fdef s3-object-summary->event-count
        :args (s/cat :config ::model/config
                     :obj-summary ::model/s3-object-summary)
        :ret int?)

(defn s3-object-summary->event-count
  "Eagerly consumes objects contents, through baldr-seq and returns a count of the events"
  [{:keys [bucket-name
           region]
    :as config}
   s3-object-summary]
  (let [s3-object (s3/get-object {:endpoint region}
                                 {:bucket-name bucket-name
                                  :key (:key s3-object-summary)})]
    (with-open [^InputStream in (:object-content s3-object)]
      (count
       (baldr/baldr-seq in)))))
