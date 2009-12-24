#Constraints

Episode 004

This uses code from the following repository:

	http://github.com/francoisdevlin/devlinsf-clojure-utils
	
In this episode we're going to discuss yet another addition in Clojure 1.1, constraints.  Constraints
are certain pre and post conditions that need to be met in order for a function to execute properly.  If 
you've used D or Eiffel you've worked with constraints before.  Let's see what a constraints looks like
in Clojure.

	Note: Show add
	
Here you can see a very simple fn, add.  It takes two numbers in, and adds them.  What is different is 
this map after the input parameters.  You can see that there is a this call to even? associated with
:pre.  This is a pre constraint.  It's a condition that must be true or an exception will be thrown. In
our case x must be even.

	Note: Switch to REPL.
	
As you can see, calling (add 2 2) works just fine.  However, if I try calling (add 1 2) and exception is
thrown.  This is what happens when a precondition is not met.

Clojure also has a method for applying post conditions.  Simply add entry with a key :post to provide a 
post condition.  Let's enforce the output to be positive.

	Note: Add :post  [(pos? %)]
	
Notice that pos? take a % as an argument.  This is a place holder for the output of the fn.  Let's run a
quick test at the REPL.  

	Note: Switch to REPL
	
As you can see, when the output is positive the function works normally.  However, when the output is 
negative, an exception is thrown.  This is the exact same behavior that the pre constraints have.

There's on more point I want to touch on briefly before I move forward.  Constraints are part of the 
fn, and therefore calls to apply, partial and comp all work as you would expect.  As you can see, this
anonymous fn works just like our add example.

	Note:  Show anon. add

Now that we've got a good feel for constraints, I'd like to discuss how to use them in practice.  There
was a blog entry posted by Fogus where he suggested that constraints should be separated from the functions
that use them (You can find the link in the show notes).  I'd like to take his idea a step further.  I say 
that in almost all cases you should always decouple your constraints from your application code.  Let's take
our even? test as an example.

	episode-004=>(defn even-constraint
	  [f & args]
	  {:pre [(even? (first args))]}
	  (apply f args))
	
Hold on tight, this next part is going to move fast.  We're going to apply this is a generic constraint 
to any input function.

	episode-004=>(even-constraint + 1 2)
	episode-004=>(even-constraint - 1 2)
	episode-004=>(even-constraint * 1 2)
	episode-004=>(even-constraint / 1 2)
	
We've now separated the constraint from the function.  We can now use this constraint anywhere, with a simple
call to partial

	(def even-add (partail even-constraint +))
	
And we could do the same for the other functions as well.  Of couse, we're just getting warmed up.  It turns out
that you can close over values in a constraint.  In other words, we can write a constraint generator function.

	episode-004=>(defn make-constraint
  	  [pred]
  		(fn [f & args]
    		{:pre [(pred (first args))]}
    		(apply f args)))

This function simply takes a predicate, an applies it to the first argument of the function.  So, the only thing we
need to supply as a developer is the predicate.

	episode-004=>((make-constraint even?) + 1 2)
	episode-004=>((make-constraint odd?) + 1 2)
	episode-004=>((make-constraint pos?) + 1 2)

This approach is good, but it's still a little limited.  The constraint is only applied to the first argument.  Fortunately, 
Clojure is a lisp and there are ways to design around this too.  I wrote to functions, pre-constraint and post-constraint just
for this very purpose.  They both generate constraint closures, with one modification to our version.  Let me show you how 
this works.  

We'll start by defining the closures with a simple predicate.  We can use them at the REPL like so.

	episode-004=>(even-in + 1 2)
	episode-004=>(even-in + 1 1)
	
Now, these closures default to testing the last argument, not the first.  Provide a leading integer before the function, however, 
and the argument at that index is tested.
	
	episode-004=>(even-in 1 + 1 2)
	episode-004=>(even-in 1 + 2 1)
	
It's also very easy to chain several small constraints into a compound constraint.

	episode-004=>(pos-out odd-in even-in 1 + 1 2)

Now, the only thing to to remember is that these constraints are combined with partial, not comp.  That is because the
constraint fn need visibility to the arguments of the regular fns.
	
So, that's how you completely decouple your constraints from your application logic.  I know it's been a lot and I probably 
missed something.	I hope this helps you write cleaner code & have more fun doing it.  Thanks for stopping by this time.  I'm
Sean Devlin, and this is Full Disclojure.