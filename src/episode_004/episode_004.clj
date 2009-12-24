(ns episode-004
  (:use lib.sfd.constraints))

(defn add
  [x y]
  {:pre [(even? x)]}
  (+ x y))

;;Anonymous fns
(fn [x y]
  {:pre [(even? x)]
   :post [(pos? %)]}
  (+ x y))

;;Constraints can be decoupled
(defn even-constraint
  [f & args]
  {:pre [(even? (first args))]}
  (apply f args))

;;Can be closed over!!
(defn make-constraint
  [pred]
  (fn [f & args]
    {:pre [(pred (first args))]}
    (apply f args)))



(def even-in (pre-constraint even?))
(def odd-in (pre-constraint odd?))
(def pos-in (pre-constraint pos?))
(def mod-5-in (pre-constraint (comp zero? #(mod % 5))))

(def even-out (post-constraint even?))
(def odd-out (post-constraint odd?))
(def pos-out (post-constraint pos?))
(def mod-5-out (post-constraint (comp zero? #(mod % 5))))

(def add-10 (partial even-out pos-out mod-5-out +))
