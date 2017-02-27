(ns statsd-client.core
  (:require [com.stuartsierra.component :as component])
  (:import [com.timgroup.statsd NonBlockingStatsDClient]))

(defprotocol Metrics
  (count [this key] "Increment a counter")
  (gauge [this key val] "Set gauge value")
  (timing [this key val] "Record timing")
  (event [this key name] "Record an event")
  ;; TODO: with-timing macro
  )


(defrecord StatsdComponent [host port prefix client]
    component/Lifecycle
    (start [c]
      (assoc c :client (NonBlockingStatsDClient. prefix host port)))

    (stop [c]
      (assoc c :client nil))

    Metrics
    (count [c key]
      (.incrementCounter client key))

    (gauge [c key val]
      (.recordeGaugeValue client key val))

    (timing [c key val]
      (.recordExecutionTime client key val))

    (event [c key val]
    (.recordSetEvent client key val)))

(defn create [opts]
  (map->StatsdComponent (merge opts {:client nil})))
