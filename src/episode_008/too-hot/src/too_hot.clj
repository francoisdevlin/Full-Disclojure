(ns #^{
       :doc "This is a sample program that coverts celcius to farenhiet.
Its main purpose is to serve as a quick example of how to write a gui in
Clojure.  Today we are also using it as a tool to demonstrait Leiningen,
Autodoc, and Clojars."}
  too-hot
  (:import [javax.swing JFrame JLabel JTextField JButton]
	   [java.awt.event ActionListener]
	   [java.awt GridLayout])
  (:gen-class))

(defn parse-double 
  "This function takes a string as an input and creates a double"
  [s]
  (Double/parseDouble s))

(defn c->f
  "This fn converts celcius to farenheit."
  [c]
  (-> c (* 1.8) (+ 32)))

(defn create-action-listener
  "Takes a fn f and build an ActionListner Proxy around it. f must take
exactly one input."
  [f]
  (proxy [ActionListener] []
    (actionPerformed [evt] (f evt))))

(defn center-item
  "Centers a component on the screen."
  [component]
     (let [cw (.getWidth component)
	   ch (.getHeight component)
	   screen (.getScreenSize (java.awt.Toolkit/getDefaultToolkit))
	   sw (.width screen)
	   sh (.height screen)]
       (.setLocation component (/ (- sw cw) 2) (/ (- sh ch) 2))))


(defn -main [& args] 
  (let [frame (JFrame. "Celsius Converter")
	input-text (JTextField. "0")
	celsius-label (JLabel. "Celsius")
	convert-button (JButton. "Convert")
	fahrenheit-label (JLabel. "Fahrenheit")
	converter-fn (fn [evt]
		       (let [c (parse-double (.getText input-text))]
			 (.setText fahrenheit-label (str (c->f c) " Fahrenheit"))))
	converter-listener (create-action-listener converter-fn)]
    (.addActionListener convert-button converter-listener)
    (.addActionListener input-text converter-listener)
    (doto frame 
      (.setDefaultCloseOperation (JFrame/EXIT_ON_CLOSE))
      (.setLayout (new GridLayout 2 2 3 3))
      (.add input-text)
      (.add celsius-label)
      (.add convert-button)
      (.add fahrenheit-label)
      (.setSize 300 80)
      (.setTitle "Too Hot!")
      (.setVisible true)
      (center-item))))