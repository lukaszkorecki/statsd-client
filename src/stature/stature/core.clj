(ns stature.core
  (:refer-clojure :exclude [count])
  (:require [com.stuartsierra.component :as component])
  (:import [com.timgroup.statsd NonBlockingStatsDClient]))

(defprotocol Metrics
  (count [this key] "Increment a counter")
  (gauge [this key val] "Set gauge value")
  (timing [this key val] "Record timing"))

(def ^:final ^:private empty-tags (make-array String 0))

(defrecord StatsdComponent [host port prefix client]
  component/Lifecycle
  (start [c]
    (let [client (NonBlockingStatsDClient. prefix host port)]
      (assoc c :client client)))
  (stop [c]
    (when (:client c)
      (.stop ^NonBlockingStatsDClient client))
    (assoc c :client nil))
  Metrics
  (count [c key]
    (.incrementCounter ^NonBlockingStatsDClient client
                       ^String (name key)
                       empty-tags)
    key)
  (gauge [c key val]
    (.gauge ^NonBlockingStatsDClient client
            ^String (name key)
            ^double val
            empty-tags)
    val)
  (timing [c key val]
    (.recordExecutionTime ^NonBlockingStatsDClient client
                          ^String (name key)
                          ^double val
                          empty-tags)
    val))

(defn create
  "Create a new Statsd client. Options map:
  - host - String
  - port - Number
  - prefix - String"
  [opts]
  {:pre [(string? (:host opts))
         (number? (:port opts))
         (string? (:prefix opts))]}
  (map->StatsdComponent (merge opts {:client nil})))

(defmacro with-timing
  "Nice macro to record timing of a given form."
  [^StatsdComponent statsd ^String key & body]
  `(let [start-time# ^Long (System/currentTimeMillis)
         return# (do
                   ~@body)
         time# ^Long (- (System/currentTimeMillis) start-time#)]
     (timing ~statsd ~key time#)
     return#))

(defmacro count-on-exception
  "Evaluates the body and if an exception is thrown
   it increments a counter and re-throws it"
  [^StatsdComponent statsd ^String key & body]
  `(try
     (do
       ~@body)
     (catch Exception err#
       (count ~statsd ~key)
       (throw err#))))
