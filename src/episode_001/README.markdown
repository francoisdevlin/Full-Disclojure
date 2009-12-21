#Full Disclojure

Episode 001

In this episode we'll be discussing two new features in Clojure 1.1, futures & promises. Futures 
are tools for executing expressions in a different thread, and reading a result later.  A promise
is a tool for storing data, and delaying program flow until the value is set.

#Futures

Futures are useful for long running tasks that should not block the REPL.  Some examples of when to
use futures include

* Creating more responsive UIs
* Executing long tasks, like network calls
* Side effects that should be run, but the result is not important.

Let's begin with an example.  Here we have some code that creates a future object, and then immediately
shows a dialog box.

	(do
		(future
			(Thread/sleep 5000)
			(sounds/play-file test-wav))
		(messages/plain-message "From the present."))
		
What happened was that when the future object was created, the code immediately began execution in another 
thread.  This is why the dialog box showed up immediately.

