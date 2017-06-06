(ns statsd-client.core
  (:require [com.stuartsierra.component :as component])
  (:import [com.timgroup.statsd NonBlockingStatsDClient]))

(defprotocol Metrics
  (count [this key] "Increment a counter")
  (gauge [this key val] "Set gauge value")
  (timing [this key val] "Record timing"))

(def blank-tags (into-array String ""))

(defrecord StatsdComponent [host port prefix client]
    component/Lifecycle
    (start [c]
      (assoc c :client (NonBlockingStatsDClient. prefix host port)))

    (stop [c]
      (assoc c :client nil))

    Metrics
    ;; TODO: implement variants with tags and sample rates
    (count [c key]
      (.incrementCounter ^NonBlockingStatsDClient client
                         ^String key
                         blank-tags))

    (gauge [c key val]
      (.recordGaugeValue ^NonBlockingStatsDClient client
                         ^String key
                         val
                         blank-tags))

    (timing [c key val]
      (.recordExecutionTime ^NonBlockingStatsDClient client
                            ^String key
                            val
                            blank-tags)))

(defn create [opts]
  (map->StatsdComponent (merge opts {:client nil})))

(defmacro with-timing [statsd key & body]
  `(let [start-time# (System/currentTimeMillis)
        return# (do
                  ~@body)

        time# (- (System/currentTimeMillis) start-time#)]

     (timing ~statsd ~key time#)
     return#))
