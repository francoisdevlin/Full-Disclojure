#Full Disclojure

Episode 001

This uses code from the following repository:

	http://github.com/francoisdevlin/devlinsf-clojure-utils

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
			(Thread/sleep 3000)
			(sounds/play-file test-wav))
		(messages/plain-message "From the present."))
		
What happened was that when the future object was created, the code immediately began execution in another 
thread.  This is why the dialog box showed up immediately, and the sound had a delay.

Sometimes you need to get at the result of a future object.  Let's try that now.  First, we'll define our
future object

	(def a-future
	     (future
	     	(Thread/sleep 10000)
	       	"The future is complete")))
	
Next, we'll dereference our future object at the REPL.
	
	episode-001=>@a-future
	
Notice how the REPL is hung.  That is because the thread that dereferences a future will be blocked 
until the future object is complete.  

	Note: Wait for dialog
	
The result of a future is cached.  This means that if we call it a second time, the result is available instantly.

	episode-001=>@a-future
	
There are a few utility functions available for interacting with futures.  The first is future-done?, which determines
if the future is completed execution.

	episode-001=>(future-done? a-future)
	
There are two other interesting functions, `future-cancel` and `future-canlled?`.  Let's see them in action.

	Note:  Restart a-future
	
	episode-001=>(future-cancel a-future)
	
I just called `future-cancel` on our future object.  This stops execution of the thread.  We can use the `future-cancelled?`
function to display that the future was cancelled.

	episode-001=>(future-cancelled? a-future)
	
If we deref `a-future` after it is cancelled, an exception is thrown.

	episode-001=>@a-future
	
If a future is cancelled, you still have to take responsibility for cleaning up your own side effects.  As such, future-cancel
should be used wisely.  

#Promises

Now we will move on to a second feature, promises.  A promise object is created by the `promise` function.  

	episode-001=>(def a-promise (promise))
	
In order to set the value of a-promise, use the deliver functions

	episode-001=>(deliver a-promise :fred)
	
And if you want to read a promise simply dereference it.

	episode-001=>@a-promise
	
The interesting thing is about promise is that it will block the thread that dereferences it if it hasn't been set
yet.  Let's redefine the promise

	episode-001=>(def a-promise (promise))

Now, I'm going to deliver a value to the promise using a future and deference it immediately.

	episode-001=>(do 
		(future (Thread/sleep 5000) (deliver a-promise :fred))
		@a-promise)
	
Notice the the REPL is hung.  It will stay this way until the promise is delivered.

	Note:  Wait fo finish
	
And there it is.  One more thing to note about promises is that the can be set only once.

	episode-001=>(deliver a-promise :lucy)
	
	episode-001=>@a-promise
	
Well, that wraps up this episode.  I hope this helps you under these two new features in Clojure 1.1.  I'm Sean Devlin, and
this is Full Disclojure.
