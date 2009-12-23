(ns episode-003
  (:use clojure.template
	clojure.test
	lib.sfd.timing
	lib.sfd.stat-utils))

;;====================
;; use in are macro
;;====================
(deftest test-first
  (are [input result] 
       (= (first input) result)
       [:a :b :c] :a
       "abc" \a
       '(:a :b :c) :a))

;;Result of (macroexpand-1 '(are ...
(do-template [input result] 
	     (is (= (first input) result)) 
	     [:a :b :c] :a
	     "abc" \a 
	     (quote (:a :b :c)) :a)

;;Result of (macroexpand '(are ...
(do 
  (is (= (first [:a :b :c]) :a))
  (is (= (first "abc") \a)) 
  (is (= (first '(:a :b :c)) :a)))

;;======================
;;Define the experiments
;;======================
(do-template
 [fn-name init-form mod-form return-form]

 (defn fn-name [n]
   (loop [i 0 c init-form]
     (if (< i n)
       (recur (inc i) mod-form)
       return-form)))

 vrange  []              (conj  c i)      c
 vrange2 (transient [])  (conj! c i)      (persistent! c)
 srange  #{}             (conj  c i)      c
 srange2 (transient #{}) (conj! c i)      (persistent! c)
 mrange  {}              (assoc  c i nil) c
 mrange2 (transient {})  (assoc! c i nil) (persistent! c))

;;======================
;;Run the experiments
;;======================
(do-template 
 [future-name test-fn]

 (def future-name
      (future
	((juxt mean stdev) 
	 (time* 100 (let [v (test-fn 100000)])))))

 classic-vec vrange
 trans-vec   vrange2
 classic-set srange
 trans-set   srange2
 classic-map mrange
 trans-map   mrange2)