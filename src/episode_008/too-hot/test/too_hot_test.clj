(ns too-hot-test
  (:use clojure.test
	too-hot))

(deftest test-c->f
  (are [c f] (= f (c->f c))
       0 32
       100 212))

(deftest test-double-parsing
  (are [s d] (= d (parse-double s))
       "0" 0
       "100" 100))