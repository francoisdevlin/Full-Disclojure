(ns episode-002
  (:use lib.sfd.timing
	lib.sfd.stat-utils))

(-> {}
    (assoc :a 1)
    (assoc :b 2)
    (assoc :c 3))

(-> (transient {})
    (assoc! :a 1)
    (assoc! :b 2)
    (assoc! :c 3)
    persistent!)

;;Wrong way to use transients!
(let [c (transient {})]
  (do
    (assoc! c :a 1)
    (assoc! c :b 2)
    (assoc! c :c 3)
    (persistent! c)))

;;This example breaks
(let [a (transient {})] 
  (dotimes [i 20] (assoc! a i i))
  (persistent! a))

;;Proper way to use transients
(persistent! 
 (reduce (fn[m v] (assoc! m v v))
	 (transient {})
	 (range 1 21)))

;;Designed to throw an exception after persistent!
(let [c (transient #{})]
  (persistent! c)
  (conj! c :a))

;;----------------------------
;; PERFORMANCE EXPERIMENTS
;;----------------------------

(defn vrange [n]
  (loop [i 0 c []]
    (if (< i n)
      (recur (inc i) (conj c i))
      c)))
 
(defn vrange2 [n]
  (loop [i 0 c (transient [])]
    (if (< i n)
      (recur (inc i) (conj! c i))
      (persistent! c))))

(defn srange [n]
  (loop [i 0 c #{}]
    (if (< i n)
      (recur (inc i) (conj c i))
      c)))
 
(defn srange2 [n]
  (loop [i 0 c (transient #{})]
    (if (< i n)
      (recur (inc i) (conj! c i))
      (persistent! c))))

(defn mrange [n]
  (loop [i 0 c {}]
    (if (< i n)
      (recur (inc i) (assoc c i nil))
      c)))
 
(defn mrange2 [n]
  (loop [i 0 c (transient {})]
    (if (< i n)
      (recur (inc i) (assoc! c i nil))
      (persistent! c))))	    

(def classic-vec
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (vrange 100000)])))))

(def trans-vec
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (vrange2 100000)])))))

(def classic-set
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (srange 100000)])))))

(def trans-set
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (srange2 100000)])))))

(def classic-map
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (mrange 100000)])))))

(def trans-map
 (future
   ((juxt mean stdev) 
    (time* 100 (let [c (mrange2 100000)])))))



