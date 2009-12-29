(ns episode-005
  (:use clojure.walk))

(defn c-to-f [c]
  (* (+ c 32) 1.8))

(defn c->f [c]
  (-> c (+ 32) (* 1.8)))
 

(defn square [n]
  (reduce +
	  (filter odd?
		  (range 0
			 (* 2 n)))))

(defn square->> [n]
  (->> n
       (* 2)
       (range 0)
       (filter odd?)
       (reduce +)))