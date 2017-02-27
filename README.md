# statsd-client
Component friendly statsd client for Clojure


### Usage

```clojure


(require '[statsd-client.core :as statsd]
         '[com.stuartsierra.component :as component])


(def metrics (-> (statsd/create {:host "statsd.internal" :port 8122 :prefix *ns*})
                 (component/start)))



(statsd/count metrics "foo.bar")
(statsd/gauge metrics "foo.baz" 42)
(statsd/event metrics "deploy" "hi")

```
