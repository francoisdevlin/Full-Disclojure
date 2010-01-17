(ns episode-006)

(defn quadratic
  "Used to simulate any quadratic fn at a point x"
  [a b c x]
  (+ (* a x x) (* b x) c))

(def our-parabola (partial quadratic 1 1 1))

(defn left-shift
  "This is a left shift of one unit."
  [x]
  (+ 1 x))

(def transformed-parabola
     (comp
      our-parabola
      left-shift))

(def transformed-parabola
     (comp
      (partial quadratic 1 1 1)
      (partial + 1)))

;;-----------------------
;; revisit square-fn
;;-----------------------
(def square-free
     (comp
      (partial reduce +)
      (partial filter odd?)
      (partial range 0)
      (partial * 2)))

(def & comp)
(def p partial)

(def square-free
     (& (p reduce +)
	(p filter odd?)
	(p range 0)
	(p * 2)))

;;----------------------
;; Anonymous square-free
;;----------------------
(map (& (p reduce +) 
	(p filter odd?) 
	(p range 0)
	(p * 2)) 
     [0 1 2 3 4 5])

;;----------------------
;; Converts a string to an int
;;----------------------
(defn parse-int [s] (Integer/parseInt s))

((& (p map (& (p reduce +) 
	      (p filter odd?) 
	      (p range 0)
	      (p * 2)
	      parse-int
	      str))
    (p filter (& even?
		 parse-int
		 str)))
     "012345")


((& (p map (& (p reduce +) 
	      (p filter odd?) 
	      (p range 0) 
	      (p * 2) 
	      parse-int
	      str))
    (p filter (& even? 
		 parse-int 
		 str)))
 "012345")
