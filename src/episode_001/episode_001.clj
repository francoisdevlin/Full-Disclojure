(ns episode-001
  (:require [lib.sfd.swing [messages :as messages]]
	    [lib.sfd [sound-utils :as sound]]))

(def test-wav "/Users/devlinsf/Documents/message.wav")

(do
  (future
    (Thread/sleep 3000)
    (sound/play-file test-wav))
  (messages/plain-message "From the Present"))

(def f
     (future
       (Thread/sleep 15000)
       "The future is complete"))

(do 
  (future (Thread/sleep 5000)
	  (deliver p :fred))
  @p)



