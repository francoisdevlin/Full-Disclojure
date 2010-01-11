(ns episode-006)

(defn quadratic
  [a b c x]
  (+ (* a x x) (* b x) c))

(def a-parabola (partial quadratic 1 1 1))

(defn x-transform
  [u]
  (+ 1 u))

(def transformed-parabola
     (comp
      a-parabola
      x-transform))

(def transformed-parabola
     (comp
      (partial quadratic 1 1 1)
      (partial + 1)))

(def square-free
     (comp
      (partial reduce +)
      (partial filter odd?)
      (partial range 0)
      (partial * 2)))

(def & comp)
(def p partial)

(def square-free
     (&
      (p reduce +)
      (p filter odd?)
      (p range 0)
      (p * 2)))

(map (& (p reduce +) (p filter odd?) (p range 0) (p * 2)) [0 1 2 3 4 5])

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
