(defproject stature "0.1.0-SNAPSHOT"
  :description "Componentized StatsD client for Clojure"
  :url "https://github.com/lukaszkorecki/stature"
  :license {:name "MIT"
            :url "https://raw.githubusercontent.com/lukaszkorecki/stature/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.datadoghq/java-dogstature "2.3"]])
