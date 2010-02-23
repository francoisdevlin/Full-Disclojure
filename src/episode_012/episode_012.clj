(ns episode-012
  (:use lib.sfd.pred-utils))

;;-------------------
;; From episode 11
;;-------------------

(defn newton
  "Creates a newton iterator.  If start is not provided, zero is assumed."
  ([f f-prime] (newton f f-prime 0))
  ([f f-prime start]
     (iterate (fn[x]
		(- x 
		   (/ (f x) 
		      (f-prime x))))
	      start)))

(defn -sqr [a b] (let [d (- a b)] (* d d)))
 
(defn -norm [a b] (Math/sqrt (-sqr a b)))

(defn simple-converge
  "Finds the first element of coll where the norm of two consecutive
elements is less than epsilon."
  ([epsilon coll] (simple-converge epsilon -norm coll))
  ([epsilon norm coll]
     (ffirst 
      (drop-while 
       (fn [[a b] & more] (< epsilon (norm a b)))
       (partition 2 1 coll)))))

(defn richardson
  "Creates a closure that computes the derivative using a Richardson
interpolation"
  [f delta]
  (fn[x](/
	 (reduce +
		 (map * [1 -8 8 -1] (map #(f (+ x (* delta %))) [-2 -1 1 2])))
	 (* 12 delta))))

;;-------------------------
;; A convenience fn to solve equations
;;-------------------------

(defn solve-equation
  "This solves a constraint fn with respect to free-var (a keyword).
  Typically constraints end with a * suffix."
  [constraint free-var a-map]
  (let [target-fn (fn [x] (constraint (assoc a-map free-var x)))
	target-prime (richardson target-fn 0.01)]
    (simple-converge 0.01 (newton target-fn target-prime))))

;;-------------------------
;; Helper fns to determine which variable is free
;;-------------------------
(defn find-all-free-keys
  "This is a utility fn to determine which keys are free in a map.
  It is used to determine which variable to solve for."
  [source-keys a-map]
  (let [frozen-keys (map first (filter (every-pred? 
					second 
					(comp (set source-keys) first))
				       a-map))
	remaining-keys (remove (set frozen-keys) source-keys)]
    remaining-keys))

(defn find-free-key
  "This is a utility fn to determine which keys are free in a map.
  It is used to determine which variable to solve for."
  [source-keys a-map]
  (let [remaining-keys (find-all-free-keys source-keys a-map)]
    (if (= (count remaining-keys) 1)
      (first remaining-keys))))

;;---------------------------
;; Our equation solving macro
;;---------------------------
(defmacro defequation
  "Creates an equation.  This macro defines three functions.

  * name*, which is the actual equation.  It should be equal to zero.
  * name-val, a closure to determine the actual value of the free fn.
  * name, which returns a map.  The result of sym-val is assoc'd with 
  the free vairable."
  ([name binding left right]
     (let [name* (symbol (str name "*"))
	   name-val (symbol (str name "-val"))	
	   keywords (vec (map keyword binding))
	   binding-map (zipmap binding keywords)
	   free-key (gensym "free-key_")]
       `(do
	  (defn ~name* [~binding-map]
	    (- ~left ~right))
	  (defn ~name-val [~'constraints-map]
	    (let [~free-key (find-free-key ~keywords ~'constraints-map)]
	      (if ~free-key (solve-equation ~name* ~free-key ~'constraints-map))))
	  (defn ~name [~'constraints-map]
	    (let [~free-key (find-free-key ~keywords ~'constraints-map)]
	      (if ~free-key (assoc ~'constraints-map ~free-key (~name-val ~'constraints-map)))))
	  ))))

;;---------------------
;; Temperature fns
;;---------------------
(defn c->f [c]
  (-> c 
      (* 1.8)
      (+ 32) 
      ))

(defn f->c [f]
  (-> f
      (- 32)
      (/ 1.8)
      ))

(defequation f<->c
  [f c]
  f
  (-> c (* 1.8) (+ 32))
  )