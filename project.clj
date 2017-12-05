(defproject kixi.event-log-guardian "0.1.0-SNAPSHOT"
  :description "Event Kinesis to S3 journey checker."
  :url "http://github.com/MastodonC/kixi.event-log-guardian"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[aero "1.1.2"]
                 [org.clojure/clojure "1.9.0-RC2"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot [kixi.event-log-guardian]
                       :uberjar-name "kixi.event-log-guardian-standalone.jar"}}
  :main kixi.event-log-guardian)
