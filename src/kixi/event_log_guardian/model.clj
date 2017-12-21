(ns kixi.event-log-guardian.model
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::bucket-name
  (s/with-gen
    (s/and string?
           #(re-matches #"^(?:staging|prod)-witan-event-log(?:-\d{8})?$" %))
    #(gen/elements ["staging-witan-event-log"
                    "prod-witan-event-log"])))
(s/def ::key
  (s/and string?
         #(re-matches #"20\d\d/[0-1]\d/[0-3]\d/[0-2][0-9]/staging-witan-event-delivery-\d-20\d\d-[0-1]\d-[0-3]\d-[0-2]\d-[0-5]\d-[0-5]\d-[a-f\d]{8}-[a-f\d]{4}-[a-f\d]{4}-[a-f\d]{4}-[a-f\d]{12}" %)))

"{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"

(s/def ::region #{"eu-central-1" "eu-west-1"})

(s/def ::s3-object-summary
  (s/keys :req [::bucket-name
                ::key]))

(defn -integer?
 [x]
 (cond (string? x) (Integer/valueOf x)
       (clojure.core/integer? x) x
       :else ::s/invalid))

(s/def ::days-in-the-past (s/conformer -integer?))

(defn time?
  [x]
  (instance? org.joda.time.DateTime x))

(s/def ::config
  (s/keys :req-un [::bucket-name
                   ::region
                   ::stream-name]))
