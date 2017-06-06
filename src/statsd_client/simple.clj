(ns statsd-client.simple
  (:require [statsd-client.core :as statsd]))

(def client (atom nil))

(defn init! [opts]
  (reset! client (.start (statsd/create opts))))

(defn stop! []
  (reset! client (.stop @client)))

(defn count [key]
  (statsd/count @client key))

(defn gauge [key value]
  (statsd/gauge @client key value))

(defn timing [key value]
  (statsd/timing @client key value))
