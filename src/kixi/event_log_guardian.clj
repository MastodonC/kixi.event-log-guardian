(ns kixi.event-log-guardian
  (:gen-class)
  (:require [aero.core :as aero]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]))

(s/def ::config
  (s/keys))

(defn validate-config
  [config]
  (if (s/valid? ::config config)
    [true (s/unform ::config (s/conform ::config config))]
    [false (s/explain-data ::config config)]))

(defn execute
  [config]
  (println "Configuration: ")
  (pprint config))

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
