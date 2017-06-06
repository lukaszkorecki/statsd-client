(defproject statsd-client "0.1.0-SNAPSHOT"
  :description "Componentized StatsD client for Clojure"
  :url "https://github.com/lukaszkorecki/statsd-client"
  :license {:name "MIT"
            :url "https://raw.githubusercontent.com/lukaszkorecki/statsd-client/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.datadoghq/java-dogstatsd-client "2.3"]])
