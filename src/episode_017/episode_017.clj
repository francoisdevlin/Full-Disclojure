(ns episode-017)

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


(defn juxt* [& args]
  (fn [& x] (map #(apply % x) args)))