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

## TODO

- [ ] tests :-)
- [ ] `with-timing` macro for easier timings recording
- [ ] `statsd-client.simple` ns for environments which don't use component:

```clojure

(require '[statsd-client.simple :as statsd])

(def conf {:host "127.0.0.1" :port 8125 :prefix "test" })
(statsd/init! conf)
(statsd/count "foo.bar")
(statsd/gauge "bar.baz" 1337)

```

## License

2017, Łukasz Korecki, licenced under MIT license.
See LICENSE for more details
