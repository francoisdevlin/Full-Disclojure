(ns episode-016
  (:use clojure.set
	clojure.template)
  (:require [clojure.contrib [seq :as seq-utils]]))

(defn inner-style
  [left-keys right-keys]
  (intersection (set left-keys) (set right-keys)))

(defn left-outer-style
  [left-keys right-keys]
  left-keys)

(defn right-outer-style
  [left-keys right-keys]
  right-keys)

(defn full-outer-style
  [left-keys right-keys]
  (union (set left-keys) (set right-keys)))


(defn join-worker
  "This is an internal method to be used in each join function."
  ([join-style left-coll right-coll join-fn]
     (join-worker join-style left-coll right-coll join-fn join-fn))
  ([join-style
    left-coll right-coll
    left-fn   right-fn]

     
     (let [indexed-left (seq-utils/group-by left-fn left-coll)
	   indexed-right (seq-utils/group-by right-fn right-coll)
	   
	   desired-joins (join-style (keys indexed-left) (keys indexed-right))
	   cross-fn (fn [joined-value]
			     (for [left-side  (get indexed-left  joined-value [{}])
				   right-side (get indexed-right joined-value [{}])]
			       (merge left-side right-side)))]
       (apply concat (map cross-fn desired-joins)))))



(do-template
 [join-name join-style]
 (defn join-name
   ([left-coll right-coll join-fn]
      (join-worker join-style left-coll right-coll join-fn))
   ([left-coll right-coll left-fn right-fn]
      (join-worker join-style left-coll right-coll left-fn right-fn)))
 inner-join       inner-style
 left-outer-join  left-outer-style
 right-outer-join right-outer-style
 full-outer-join  full-outer-style)

(defn natural-join
  "Performs the natural join.  If there are no keys that intersect, the join is not performed."
  [left-coll right-coll]
  (let [intersect (apply intersection
			 (map (comp set keys first)
			      [left-coll right-coll]))]
    (if (empty? intersect)
      []
      (inner-join left-coll right-coll (apply juxt intersect)))))
	
(defn cross-join
  "CLOJURE IS AWESOME"
  [left-coll right-coll]
  (inner-join left-coll right-coll (constantly 1)))

;;----------------------
;; SAMPLE DATA
;;----------------------
(def from-xml 
     [{:name "Sean" :age 28} 
      {:name "Ross" :age 28} 
      {:name "Brian" :age 23}])

(def from-sql 
     [{:owner "Sean" :item "Beer"} 
      {:owner "Sean" :item "Pizza"}
      {:owner "Ross" :item "Computer"}
      {:owner "Matt" :item "Bike"}])

(defn display-data
  [data]
  (apply str "Name\tAge\tOwner\tItem\n"
	 (interpose "\n"
		    (map (comp
			  (partial apply str)
			  (partial interpose \tab)
			  (juxt :name :age :owner :item))
			 data))))