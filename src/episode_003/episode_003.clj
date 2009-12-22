(ns episode-003
  (:use clojure.template
	lib.sfd.timing
	lib.sfd.stat-utils))

(do-template
 [fn-name init-form mod-form return-form]
 (defn fn-name [n]
   (loop [i 0 c init-form]
     (if (< i n)
       (recur (inc i) mod-form)
       return-form)))
 vrange [] (conj c i) c
 vrange2 (transient []) (conj! c i) (persistent! c)
 srange #{} (conj c i) c
 srange2 (transient #{}) (conj! c i) (persistent! c)
 mrange {} (assoc c i nil) c
 mrange2 (transient {}) (assoc! c i nil) c)

(do-template 
 [future-name test-fn]
 (def future-name
      (future
	((juxt mean stdev) 
	 (time* 100 (let [v (test-fn 100000)])))))
 classic-vec vrange
 trans-vec vrange2
 classic-set srange
 trans-set srange2
 classic-map mrange
 trans-map mrange2)