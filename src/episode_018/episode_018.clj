(ns episode-018
  (:use clojure.test
	clojure.template
	))

(defmacro time*
  "Evaluates expr and returns the time it took.  Returns the time
as a double.  Takes an optional parameter n, and repeats the test
n times."
  ([expr]
     `(let [start# (. System (nanoTime))
	    ret# ~expr
	    stop# (. System (nanoTime))]
	(/ (double (- stop# start#)) 1000000.0)))
  ([n expr]
     `(map (fn[~'x] (time* ~expr )) (range 0 ~n))))

(def test-vec [1 2 3 4 5])

(declare rotate-imp)

(deftest rotate-spec
  (are [n] (= (rotate-imp n []) '())
       -6 -5 -4 -3 -2 -1 0 1 2 3 4 5 6)
  (are [n] (= (count (rotate-imp n test-vec)) (count test-vec))
       -6 -5 -4 -3 -2 -1 0 1 2 3 4 5 6)
  (are [n coll] (= (rotate-imp n test-vec) (seq coll))	
       -6 [5 1 2 3 4]
       -5 [1 2 3 4 5]
       -4 [2 3 4 5 1]
       -3 [3 4 5 1 2]
       -2 [4 5 1 2 3]
       -1 [5 1 2 3 4]
       0 [1 2 3 4 5]
       1 [2 3 4 5 1]
       2 [3 4 5 1 2]
       3 [4 5 1 2 3]
       4 [5 1 2 3 4]
       5 [1 2 3 4 5]
       6 [2 3 4 5 1]))

(defn rotate-original
  [n coll]
  (let [c (count coll)] 
    (take c (drop (mod n c) (cycle coll)))))

(defn rotate-lazy-cat [n coll] 
  (lazy-cat (drop n coll) 
            (take n coll))) 

(defn rotate-concat [n coll] 
  (let [shift (mod n (count coll))] 
    (concat (drop shift coll) 
            (take shift coll)))) 

(defn rotate-destruct [n coll] 
  (let [[front back] (split-at (mod n (count coll)) coll)] 
    (concat back front))) 

(defn rotate-concat-with-empty
  [n coll]
  (if (empty? coll) '()
      (let [shift (mod n (count coll))] 
	(concat (drop shift coll) 
		(take shift coll)))))

(defn rotate-destruct-with-empty [n coll] 
  (if (empty? coll) '()
      (let [[front back] (split-at (mod n (count coll)) coll)] 
	(concat back front))))

(time* 100
       (dotimes [x 1000]
	 (rotate-imp 2 test-vec)))