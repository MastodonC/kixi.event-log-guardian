(ns kixi.event-log-guardian.hour->s3-object-summaries
  (:require [amazonica.aws.s3 :as s3]
            [clojure.spec.alpha :as s]
            [clj-time.core :as t]
            [kixi.event-log-guardian.model :as model]))


(defn hour->s3-prefix
  [hour]
  (->> [(t/year hour)
        (t/month hour)
        (t/day hour)
        (t/hour hour)]
       (map str)
       (map #(if (= 1 (count %))
               (str "0" %)
               %))
       (interpose "/")
       (apply str)))

(def max-objects 20)

(s/fdef hour->s3-object-summaries
        :args (s/cat :s3-config (s/keys :req-un [::model/bucket-name
                                                 ::model/region])
                     :hour t/hours?)
        :ret (s/coll-of ::model/s3-object-summary))

(defn hour->s3-object-summaries
  ([{:keys [bucket-name
            region]
     :as config}
    hour]
   (hour->s3-object-summaries bucket-name
                              region
                              (hour->s3-prefix hour)
                              nil))
  ([bucket-name region prefix marker]
   (let [list-objects-res (s3/list-objects {:endpoint region}
                                           (merge {:bucket-name bucket-name
                                                   :prefix prefix
                                                   :max-keys max-objects}
                                                  (when marker
                                                    {:marker marker})))]
     (concat (:object-summaries list-objects-res)
             (when (:next-marker list-objects-res)
               (hour->s3-object-summaries bucket-name region prefix (:next-marker list-objects-res)))))))
