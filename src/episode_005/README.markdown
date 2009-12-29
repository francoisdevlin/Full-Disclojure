#-> and ->>

Episode 005

In this episode we discuss a macro that took me a while to understand when I came Clojure

Explain expression threading, in context of episode has nothing to do with simultaneous code.

#Thread-first
Let's take a look at a very simple function definition.  Here we have a function designed to convert Celsius to Fahrenheit. 

	episode-005=>(defn c-to-f [c]
  	  (* (+ c 32) 1.8))

As you can see it's a very simple function definition, with nested arithmetic operations.  Let's transform this using the 
thread-first macro.  Before we do that, let's use macro expansion to understand exactly what -> does.

	episode-005=>(macroexpand '(-> c (+ 32)))
	(+ c 32)
	
What thread-first does is take the first expression and inserts it in the second spot of the second macro.  In our case, it takes
`c` and inserts it in the second spot of `(+ 32)`.  This produced the output of `(+ c 32)`.  Now, I just said that thread-first takes
the first *expression* and inserts in into the second expression.

	episode-005=>(macroexpand '(-> (+ c 32) (* 1.8)))
	(* (+ c 32) 1.8)
	
This is the exact body of the function we wrote earlier.  This is good, but in its present form, thread-first hasn't bought us 
anything yet.  Fortunately, there's more to this macro.  Thread-first is variadic.  Let's break this up and see what happens

	episode-005=>(macroexpand '(-> c (+ 32) (* 1.8)))
	(* (clojure.core/-> c (+ 32)) 1.8)
	
What happened here?  A recursive call to thread first was inserted as the second argument in the last list.  If we use macroexpand-all
from clojure.walk, we can fully expand the expression.

	episode-005=>  (macroexpand-all '(-> c (+ 32) (* 1.8)))
	(* (+ c 32) 1.8)
	
Now, we finally have a simple call to thread first that produces the exact form we originally had.  We can create a second function defined
using this macro

	episode-005=>(defn c->f [c] (-> c (+ 32) (* 1.8)))
	
Awesome!  Now, there's only one thing left to do.  Test our new function to see if it behaves properly.  We know that 0 degrees C is equal
to 32 degrees F, so let's make sure this works.

	episode-005=>(c->f 0)
	57.6
	
Uh oh.  This isn't right.  What went wrong?  I'll tell you what went wrong.  There's a bug in our original function.

	episode-005=>(c-to-f 0)
	57.6
	
It turns out I made a mistake in my algebra.  Let's correct this in the original version.  We need to switch an operator here...  two constants
there...  make sure everything is in the right spot.  Okay.  Should be good to go.

	Note:  Correct Fn

	episode-005=>(c-to-f 0)
	32.0
	
Now, let's correct the version written with the thread-first macro.  All we have to do is change the order of these expressions in the macro call.
Done.

	Note: Correct other Fn

	episode-005=>(c->f 0)
	32.0
	
This use of thread-first is very idiomatic Clojure.  As you can see, it lets us rapidly and confidently rearrange the code to get the right result.
This fits into the Clojure way of testing pure functions and iterating to a new solution very, very well.

#Thread-last
Thread first been a very successful tool in Clojure, and I believe the lisp world in general for a while.  Introduced in Clojure 1.1 is a similar 
macro, thread last.  Thread last is very similar to thread first, except that it inserts the expression into the end of the form.  Let's take a
look at some sample code to see this in action.

Here we have an alternate way to find the nth square number.  As you may recall, the sum of the first n odd numbers is equal to n squared.

	episode-005=>(defn square [n]
	  (reduce +
		  (filter odd?
			  (range 0
				 (* 2 n)))))
				
Notice how the function drags right.  We can clean up this code by using thread last.

	episode-005=>(defn square->> [n]
	  (->> n
	       (* 2)
	       (range 0)
	       (filter odd?)
	       (reduce +)))

And, as you can see both functions work just fine.


	episode-005=>(square 10)
	100
	
	episode-005=>(square->> 10)
	100
	
So, that's how you use thread-first and thread-last.  I hope this makes using these macros in you code easier.
	
