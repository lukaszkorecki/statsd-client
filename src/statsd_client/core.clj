(ns statsd-client.core
  (:require [com.stuartsierra.component :as component])
  (:import [com.timgroup.statsd NonBlockingStatsDClient NoOpStatsDClient]))

(defprotocol Metrics
  (count [this key] "Increment a counter")
  (gauge [this key val] "Set gauge value")
  (timing [this key val] "Record timing"))

(def blank-tags (into-array String ""))

(defrecord StatsdComponent [host port prefix noop client]
  component/Lifecycle
  (start [c]
    (let [client (if noop
                   (NoOpStatsDClient.)
                   (NonBlockingStatsDClient. prefix host port))]
      (assoc c :client client)))

  (stop [c]
    (assoc c :client nil))

  ;; TODO: implement variants with tags and sample rates
  Metrics
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

(defn create
  "Create a new Statsd client. Options map:
  - host - String
  - port - Number
  - prefix - String
  Optional
  - noop - if true, will use NoOpStatsDClient"
  [opts]
  {:pre [(string? (:host opts))
         (number? (:port opts))
         (string? (:prefix opts))]}
  (map->StatsdComponent (merge opts {:client nil})))

(defmacro with-timing
  "Nice macro to record timing of a given form."
  [^StatsdComponent statsd ^String key & body]
  `(let [start-time# (System/currentTimeMillis)
         return# (do
                   ~@body)

         time# (- (System/currentTimeMillis) start-time#)]

     (timing ~statsd ~key time#)
     return#))
