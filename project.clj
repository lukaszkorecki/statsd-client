(defproject stature "0.5.4"
  :description "Componentized StatsD client for Clojure"
  :global-vars {*warn-on-reflection* true}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.datadoghq/java-dogstatsd-client "2.5"]
                 [com.stuartsierra/component "0.3.2"]]

  :profiles {:dev
             {:dependencies [[aleph "0.4.4"]]}})

