(ns episode-007
  (:import (java.util Date GregorianCalendar)
	   (java.sql Timestamp)
	   (org.joda.time DateTime DateTime$Property DateTimeZone 
                          Minutes Hours Period Interval)))

;;----------------------
;; Dispatch Fn
;;----------------------
(defn- to-ms-dispatch
  [& params]
  (let [lead-param (first params)]
    (cond
     (empty? params) ::empty
     (nil? lead-param) ::nil
     (instance? java.util.Calendar lead-param) ::calendar
     true (class lead-param))))

;;---------------------
;; Convert to Long
;;---------------------
(defmulti to-ms to-ms-dispatch)

(defmethod to-ms Long
  [& params]
  (first params))

(defmethod to-ms Date
  [& params]
  (.getTime (first params)))

(defmethod to-ms Timestamp
  [& params]
  (.getTime (first params)))

(defmethod to-ms ::calendar
  [& params]
  (to-ms (.getTime (first params))))

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