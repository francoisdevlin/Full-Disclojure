(ns episode-007
  (:import (java.util Date Calendar GregorianCalendar)
	   (java.sql Timestamp)
	   (org.joda.time DateTime DateTime$Property DateTimeZone 
                          Minutes Hours Period Interval)
	   (org.joda.time.format ISODateTimeFormat DateTimeFormatter)))

;;----------------------
;; Dispatch Fn
;;----------------------
(defn- to-ms-dispatch
  [& params]
  (let [lead-param (first params)]
    (cond
     (empty? params) ::empty
     (nil? lead-param) ::nil
     true (class lead-param))))

;;---------------------
;; Convert to Long
;;---------------------
(defmulti to-ms to-ms-dispatch)

(defmethod to-ms Long
  [& params]
  (first params))

(defmethod to-ms Long
  [l]
  l)

(defmethod to-ms Calendar
  [c]
  (to-ms (.getTime c)))


(defmethod to-ms Date
  [d]
  (.getTime d))

(defmethod to-ms Timestamp
  [ts]
  (.getTime ts))

(defmethod to-ms ::empty
  [& params]
  (to-ms (Date. )))

(defmethod to-ms ::nil
  [& params]
  nil)
;;----------------------
;; Convert to Type
;;----------------------
(defn date [& params]
  (Date. (apply to-ms params)))

(defn greg-cal [& params]
  (doto (GregorianCalendar. )
    (.setTime (apply date params))))

(defn sql-ts [& params]
  (Timestamp. (apply to-ms params)))

;;---------------------
;; Usages
;;---------------------
(defn compare-time
  [a b]
  (.compareTo (date a) (date b)))

(defn before?
  "Tests to determine if time a is before time b"
  [a b]
  (= (compare-time a b) -1))

(defn after?
  "Tests to determine if time a is after time b"
  [a b]
  (= (compare-time a b) 1))

(defn some-db-fn
  [t & other-stuff]
  (update-db {:updated-at (sql-ts t)
	      :more-stuff other-stuff}))

;;---------------------
;; Extend
;;---------------------
(defmethod to-ms DateTime
  [& params]
  (.getMillis (first params)))

(defn joda [& params]
  (DateTime. (apply to-ms params)))

(defmethod to-ms ::map
  [& params]
  (let [default-map {:year 2000
		     :month 1
		     :day 1
		     :hour 0
		     :minute 0
		     :second 0
		     :ms 0}
	input-map (first params)
	resulting-map (merge default-map input-map)
	[y mo d h mi s ms] ((juxt :year :month :day :hour :minute :second :ms)
			    resulting-map)]
    (to-ms (DateTime. y mo d h mi s ms))))