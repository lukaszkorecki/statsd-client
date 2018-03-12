(ns stature.mock
  (:require [stature.core :as proto]
            [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]))

(defrecord Mock []
  component/Lifecycle
  (start [this] this)
  (stop [this] this)
  proto/Metrics
  (count [this key]
    (log/infof "%s incr" key))
  (gauge [this key val]
    (log/infof "%s g:%s" key val))
  (timing [this key val]
    (log/infof "%s t:%s" key val)))

(defn create []
  (->Mock))
