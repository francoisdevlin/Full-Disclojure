#Transients

Episode 002

This uses code from the following repository:

	http://github.com/francoisdevlin/devlinsf-clojure-utils

In today's episode, we'll be looking at transients.  Transients are Clojure's attempt
to strike a compromise between persistence and performance.  The performance improvement
is achieved because transients ignore intermediate values.

Let's start by taking a look at maps.  Here we have a normal use of Clojure's persistent 
hash-maps.  

	episode-002=>(let [c {}]
	  (-> c
	    (assoc :a 1)
	    (assoc :b 2)
	    (assoc :c 3)))
	
Let's run the same example with transients as see what happens.

	episode-002=>(let [c (transient {})]
      (-> c
     	 (assoc! :a 1)
     	 (assoc! :b 2)
     	 (assoc! :c 3)
         persistent!))
	
#Signature difference

The first thing to notice is the call to `transient`.  This converts or persistent data structure 
to a transient one.

The next function is `assoc!`.  `assoc!` takes a transient map and a key value
pair, very similar to `assoc`.  It returns a modified collection, that ignores the intermediate result.  Once
each `assoc!` has been called, the entire thing is converted back to a persistent data structure with
`persistent!`

Now, let me take a second to address some concerns you might have about transients
When I first heard about transient data structures in Clojure, I wasn't sure they were a good idea.  My initial
reaction was to criticize their inclusion in the language.  However, as I learned more about them
I warmed up to the idea.

#Concurrency
Transients are not mutable data structures.  You can not use them to write imperative code. For example,
the following code appears to work

	episode-002=>(let [c (transient {})]
  	  (do
    	(assoc! c :a 1)
    	(assoc! c :b 2)
    	(assoc! c :c 3)
    	(persistent! c)))

However, this mutating in place behavior is an implementation detail.  If we use the following example 

	episode-002=>(let [a (transient {})] 
  	  (dotimes [i 20] (assoc! a i i))
  		(persistent! a))

We can see that it doesn't work properly.  Only the first eight items were places into the array.  If
we re-write functionally with reduce everything is fine.

	episode-002=> (persistent! 
 	  (reduce (fn[m v] (assoc! m v v))
	 	(transient {})
	 	(range 1 21)))

The next reason is that transients can only be modified in the same thread they are created in.  As
you can see in this example, 

	epiode-002=>@(let [c (transient [])] (future (conj! c :a)))

an exception is thrown when a transient is modified in a separate thread.  This is because
transients are meant to be used as an exception, rather than the rule.  The intent is to use them
only as a local implementation detail.  

The third reason is that this is that it isn't possible to use a transient after it have been converted to
a persistent version.  Let's take a look at another example, this time with a hash-set

	episode-002=>(let [c (transient #{})]
  	  (-> c
 	 	(conj! :a)
 	 	(conj! :b)
 	 	(conj! :c)
     	persistent!)
		(conj! c :d))
		
Notice that an exception is thrown, preventing modification of the transient after it has been converted.

#Functions that work on transients.

By now we've seen that `conj!` and `assoc!` work on transients.  There are three other functions that work on them,
`dissoc!`, `pop!`, and `disj!`.  These functions work on transient maps, vectors, and sets respectively.

It's also worth noting that there are a few data structures transients don't work on.  If you try to call transient
on a list

	episode-002=> (transient '())
	
or a sorted set

	episode-002=> (transient (sorted-set))
	
or a sorted map

	episode-002=> (transient (sorted-map))
	
Clojure will throw an exception.  These structures are not supported in 1.1, because there is not a large performance
improvement to be had.

#Speedup
This brings us to the real question is how much faster are transients?  I ran my own experiments to
figure this out.  Here you can see several range functions, designed to stress the performance of each
transient type.

I used a modified version of the time macro, time*.  This behaves
just like the time macro, expect that a double representing the number of milliseconds is
returned instead of the result of the expression.  I'm not going to get
into the details here, but I found that transients range from being 2 to 10 times faster,
with an average of about 5x.  

#Conclusions
Is a 5x speedup significant?  I see two answers to that.

From an academic computer science view this is not something new.  You still need to make
sure that your caching is done properly, the algorithm converges quickly enough, and that
you have taken every possible step to reduce the problem size.

However, as a practitioner and engineer,  I think that a 5x speedup is wonderful.  This means
that my servers can do 5x as much work, that is to say they can make 5x more money before I need
to consider upgrading my data center.  That's amazing.

For more information I'd suggest reading the official documentation on transients, which you can find 
at clojure.org/transients.

Thanks for watching.  I"m Sean Devlin, and this is Full Disclojure