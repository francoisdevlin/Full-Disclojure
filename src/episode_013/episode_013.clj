(ns episode-013
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

;;-------------------
;; From episode 12
;;-------------------
(defn solve-equation
  "This solves a constraint fn with respect to free-var (a keyword).
  Typically constraints end with a * suffix."
  [constraint free-var a-map]
  (let [target-fn (fn [x] (constraint (assoc a-map free-var x)))
	target-prime (richardson target-fn 0.01)]
    (simple-converge 0.01 (newton target-fn target-prime))))

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
;; Inference Graph
;;---------------------
(defmacro definference
  "Creates a constraint and adds it to the supplied inference graph.
  Use this instead of defconstraint if you want to use this inference
  engine.

  inference-graph-ref is expected to be a ref to a map."
  [sym binding inference-graph-ref left right]
  (let [keywords (vec (map keyword binding))]
    `(do      
       (defequation ~sym  ~binding ~left ~right)
       (dosync (alter ~inference-graph-ref assoc ~keywords ~sym)))))


(def *max-depth* 10)

(defn- potential-fns
  "A private helper fn for inference chain."
  [values graph]
  (into {} (filter (comp (partial some (set values)) first) graph)))

(defn- usable-fns 
  "A private helper fn for inference chain."
  [current-map pot-fns]
  (into {} (filter (comp #(find-free-key % current-map) first) pot-fns)))

(defn infer-chain
  "This function does the heavy lifting for the infer method.
It inspects the input-map, and apllies any constraints that
have exactly one free variable.  Once a equation that can
find desired value is found, the resulting closure chain is
reutrned.

  This performs a depth-first search, and will return nil when
*max-depth* steps are found."
  [inference-graph desired-value input-map]
  (let [clean-map (into {} (filter second input-map))]
    (if (clean-map desired-value)
      [identity] ;Value already is in map, abort
      ((fn inference-loop [known-map moves current-iter]
	(if (not (zero? current-iter))
	  (let [end-fns (potential-fns [desired-value] inference-graph)
		end-now (usable-fns known-map end-fns)]
	    (cond
	      (empty? end-fns) nil ;Can't get to the end :(
	      (not (empty? end-now)) (conj moves (first (vals end-now))) ; I can end!
	      true (let ;Can I make a move?
		       [next-fns (potential-fns (keys known-map) inference-graph)
			next-now (usable-fns known-map next-fns)]
		     (cond
		       (empty? next-now) nil ;Can't take next step :(
		       true (first (filter identity
					   (map #(let [next-move (second %) ;I can move!
						       new-known (find-free-key (first %) known-map)]
						   (inference-loop 
						    (assoc known-map new-known 1)
						    (conj moves next-move)
						    (dec current-iter)))
						next-now)))))))))
       clean-map () *max-depth*))))

(defn infer
  [inference-graph desired-value input-map]
  (let [clean-map (into {} (filter second input-map))
	ic (infer-chain inference-graph desired-value input-map)]
    (if ic
      ((apply comp ic) clean-map))))

;;---------------------
;; Temperature fns
;;---------------------
(def gas-laws (ref {}))

(definference f<->c [f c] gas-laws f (-> c (* 1.8) (+ 32)))
(definference k<->c [k c] gas-laws k (-> c (+ 273.15)))

;;Expanded Version
(do
  (defequation f<->c [f c] f (-> c (* 1.8) (+ 32)))
  (dosync (alter gas-laws assoc [:f :c] f<->c)))

(let [ic (apply comp (infer-chain @gas-laws :k {:f 0}))]
  (map #(ic {:f %})) (range 0 100 10))