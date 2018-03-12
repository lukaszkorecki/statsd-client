(ns stature.simple
  (:refer-clojure :exclude [count])
  (:require [stature.core :as statsd]))

;; setup a client instance, but do not start it
(def client (atom
             (delay
              (.start
               (statsd/create {:host "127.0.0.1"
                               :port 8125
                               :prefix "stature"})))))

(defn stop! []
  (reset! client (.stop @@client)))

(defn init! [opts]
  (when @@client
    (stop!))

  (reset! client (delay (.start (statsd/create opts)))))

(defn count [key]
  (statsd/count @@client key))

(defn gauge [key value]
  (statsd/gauge @@client key value))

(defn timing [key value]
  (statsd/timing @@client key value))

(defmacro with-timing [key & body]
  `(let [start-time# ^Long (System/currentTimeMillis)
         return# (do
                   ~@body)

         time# ^Long (- (System/currentTimeMillis) start-time#)]

     (timing ~key time#)
     return#))

(defmacro count-on-exception
  "Evaluates the body and if an exception is thrown
   it increments a counter and re-throws it"
  [^String key & body]
  `(try
     (do
       ~@body)
     (catch Exception err#
       (count ~key)
       (throw err#))))
