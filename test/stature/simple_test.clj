(ns stature.simple-test
  (:require [stature.simple :as statsd]
            [clojure.test :refer :all]
            [stature.helper.statsd-server :as server]))

(deftest sending-metrics
  (testing "sends all kinds of metrics"
    (let [port 10029
          _ (statsd/init! {:port port :host "127.0.0.1" :prefix "test"})
          metric-store (atom [])
          statsd-server (server/start port metric-store)]

      (do
        (is (= "counter" (statsd/count "counter")))
        (Thread/sleep 500)
        (is (= 42 (statsd/gauge "gauge" 42)))
        (Thread/sleep 500)
        (is (= 20 (statsd/gauge :meh.bar.foo 20)))
        (Thread/sleep 500)
        (is (= 100 (statsd/timing :some.timing 100)))

        (Thread/sleep 500)
        (is (= 1
               (statsd/with-timing :macro.timing
                 (Thread/sleep 50)
                 1)))

        (is (thrown? ArithmeticException
                     (statsd/count-on-exception :exc.count
                                                (/ 7 0))))
        (Thread/sleep 500)

        (let [metrics (take 6 @metric-store)
              examples [["test.counter" "1|c"]
                        ["test.gauge" "42|g"]
                        ["test.meh.bar.foo" "20|g"]
                        ["test.some.timing" "100|ms"]
                        ["test.macro.timing" "50|ms"]
                        ["test.exc.count" "1|c"]]]
          (map-indexed
           (is #(= %2 (get examples %1)))))

        (server/stop statsd-server)
        (statsd/stop!)))))
