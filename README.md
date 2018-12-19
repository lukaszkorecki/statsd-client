# :warning: Heads up!

See here https://github.com/nomnom-insights/nomnom.stature for the maintained version

# stature

![](https://i.annihil.us/u/prod/marvel/i/mg/3/60/527413be6077d/standard_xlarge.jpg)

Component friendly statsd client for Clojure

# :warning: read this 

Stature has been in production use at [NomNom](https://nomnominsights.com) for a while now.
We're planing to release it to Clojars soon :soon: :tm:


### Usage

```clojure


(require '[stature.core :as statsd]
         '[com.stuartsierra.component :as component])


(def metrics (-> (statsd/create {:host "statsd.internal" :port 8122 :prefix *ns*})
                 (component/start)))



(statsd/count metrics "foo.bar")
(statsd/gauge metrics "foo.baz" 42)

(statsd/with-timing metrics "some.timing"
  (do-expensive-work))

(statsd/count-on-exception "foo.bar.failure"
  (some-remote-call-that-fails)) ;; -> will increment foo.bar.failure counter if exception is thrown

```

### Simple NS (no direct component dependency)
-
```clojure

(require '[stature.simple :as statsd])

(def conf {:host "127.0.0.1" :port 8125 :prefix "test" })
(statsd/init! conf)
(statsd/count "foo.bar")
(statsd/gauge "bar.baz" 1337)

```


### Mock component

```clojure
(require '[stature.mock]
         '[stature.core :as metrics])

(def m (mock/create))

(metrics/increment m "foo.bar")
```
