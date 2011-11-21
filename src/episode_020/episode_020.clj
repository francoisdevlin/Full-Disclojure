(ns episode-020.episode-020
  (:require [clojure.string :as s]))

(def params (ref {:x "1" :y "2"}))

(defn web-add
  []
  (let [x (:x @params)
        y (:y @params)]
    (if (or (nil? x) (s/blank? x)) (throw (Exception. "x is not valid data.")))
    (if (or (nil? y) (s/blank? y)) (throw (Exception. "y is not valid data.")))
    (let [x (Integer/parseInt x)
          y (Integer/parseInt y)]
      (+ x y))))

;;;;;;;;;;;;;;;;;;;;
; Decorator version
;;;;;;;;;;;;;;;;;;;;

(defn add [x y] (+ x y))

(defn params-decorator
  [f]
  (fn [] (f @params)))

(defn validate-decorator
  [f]
  (fn [input-params]
    (if (or (nil? (:x input-params)) (s/blank? (:x input-params))) (throw (Exception. "x is not valid data")))
    (if (or (nil? (:y input-params)) (s/blank? (:y input-params))) (throw (Exception. "y is not valid data")))
    (f input-params)))

(defn coerce-decorator
  [f]  
  (fn [input-params]
    (f (-> input-params
        (update-in [:x] #(Integer/parseInt %))
        (update-in [:y] #(Integer/parseInt %))))))

(defn unpack-decorator
  [f]
  (fn [{x :x y :y}] (f x y)))