(ns stature.core-test
  (:require [clojure.test :refer :all]
            [stature.core :as statsd]
            [stature.helper.statsd-server :as server]))

(deftest sending-metrics
  (testing "sends all kinds of metrics"
    (let [port 10029
          metrics (.start (statsd/create {:port port :host "127.0.0.1" :prefix "test"}))
          metric-store (atom [])
          statsd-server (server/start port metric-store)]

      (do
        (is (= "counter" (statsd/count metrics "counter")))
        (is (= 42 (statsd/gauge metrics "gauge" 42)))
        (is (= 20 (statsd/gauge metrics :meh.bar.foo 20)))

        (is (= 11 (statsd/with-timing metrics "timing"
                    (Thread/sleep 500)
                    11)))

        ;; wait for things
        (Thread/sleep 500)
        (is (= [["test.counter" "1|c"]
                ["test.gauge" "42|g"]
                ["test.meh.bar.foo" "20|g"]]

               (take 3 @metric-store)))

        ;; we have to skip value as it's not precise measurement
        (is (= "test.timing" (first (last @metric-store))))

        (server/stop statsd-server)
        (.stop metrics)))))

(deftest counting-exceptions
  (testing "'count-on-exception' macro incs a key on exception"
    (let [port 10029
          metrics (.start (statsd/create {:port port :host "127.0.0.1" :prefix "test-exceptions"}))
          metric-store (atom [])
          statsd-server (server/start port metric-store)]

      (do

        (is (thrown? ArithmeticException
                     (statsd/count-on-exception
                      metrics
                      :math.failure
                      (/ 7 0))))

        (Thread/sleep 500)

        (is (= 10 (statsd/count-on-exception
                   metrics
                   :math.failure
                   (statsd/gauge metrics :math.ok (/ 100 10)))))

        (Thread/sleep 500)

        (is (= [["test-exceptions.math.failure" "1|c"]
                ["test-exceptions.math.ok" "10|g"]]
               @metric-store))

        (server/stop statsd-server)
        (.stop metrics)))))
