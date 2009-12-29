(ns episode-004
  (:use lib.sfd.constraints))

(defn add
  [x y]
  {:pre [(even? y) (odd? x)]
   :post [(pos? %)]}
  (+ x y))

;;Anonymous fns
(fn [x y]
  {:pre [(even? y)]}
  (+ x y))

;;Constraints can be decoupled
(defn even-constraint
  [f & args]
  {:pre [(even? (last args))]}
  (apply f args))

;;Can be closed over!!
(defn make-constraint
  [pred]
  (fn [f & args]
    {:pre [(pred (last args))]}
    (apply f args)))

;;=============================
;; Constraint Generators
;;=============================
(def even-in (pre-constraint even?))

(def pos-out (post-constraint pos?))