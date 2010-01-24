(ns kata-001
  (:use lib.sfd.same
	lib.sfd.seq-utils
	lib.sfd.patterns
	clojure.test))

(defn parse-int [s] 
  (if (empty? s) 0 (Integer/parseInt s)))

(def delimiters #{\, \newline})

(defn custom-parse [s]
  (map parse-int
       (multi-same split delimiters s)))

(defn adder [s]
  (reduce + (custom-parse s)))

(defn adder2 [s]
  (if (#{"//"} (same take 2 s))
    (binding [delimiters (conj delimiters (nth s 2))]
      (adder (same drop-until #{\newline} s)))
    (adder s)))

(deftest test-adder2
  (are [in out] (= (adder2 in) out)
       "" 0
       "1" 1
       "1,2" 3
       "1,2,3" 6
       "1,2\n3" 6
       "//:\n1:2:3" 6
       ))