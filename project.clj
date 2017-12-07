(def metrics-version "2.7.0")
(def slf4j-version "1.7.21")
(defproject kixi.event-log-guardian "0.1.0-SNAPSHOT"
  :description "Event Kinesis to S3 journey checker."
  :url "http://github.com/MastodonC/kixi.event-log-guardian"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[aero "1.1.2"]
                 [amazonica "0.3.92" :exclusions [ch.qos.logback/logback-classic
                                                  com.amazonaws/aws-java-sdk
                                                  commons-logging
                                                  org.apache.httpcomponents/httpclient
                                                  joda-time]]
                 [baldr "0.1.1"]
                 [com.amazonaws/aws-java-sdk "1.11.53" :exclusions [joda-time]]
                 [clj-time "0.14.2"]
                 [kixi/kixi.log "0.1.5"]
                 [org.clojure/clojure "1.9.0-RC2"]
                 [org.slf4j/log4j-over-slf4j ~slf4j-version]
                 [org.slf4j/jul-to-slf4j ~slf4j-version]
                 [org.slf4j/jcl-over-slf4j ~slf4j-version]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot [kixi.event-log-guardian]
                       :uberjar-name "kixi.event-log-guardian-standalone.jar"}}
  :main kixi.event-log-guardian)
