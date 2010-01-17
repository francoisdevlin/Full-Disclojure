#Point Free Clojure

Last time we used the threading macros to reduce code size an abstract away list construction.  In this
episode I'd like to discuss a second style for combining code, point free.  Point free code is a style that
stresses partial application and functional composition.  Let's start by reviewing these concepts in a simple
fn.

	episode-006=>(defn quadratic
	  	[a b c x]
	  	(+ (* a x x) (*  b x) c))
	
Here we have a fn for evaluating any quadratic equation at any point x.  However, sometimes we want to turn this
general tool into a specific parabola.  This is where partial application comes in. Let's use the equation x^2+x+1 as an
example.

	episode-006=>(def a-parabola (partial quadratic 1 1 1))
	
	Note: Show examples
	
Now, suppose we want add a coordinate transform to this.  This happens all the time when you can't measure x directly, but
you can measure some related value u instead.  For our very simple example, we're going to use this transform

	episode-006=>(defn left-shift [x] (+ 1 x))
	
Mathematically, we want to do this

	episode-006=>(a-parabola (left-shift 0))
	
Notice that we have a chaining of fns calls here.  The mathematical term for this is composition.  Clojure provides a 
mechanism form doing this, the compose operator.

	episode-006=>(def transformed-parabola (comp a-parabola left-shift))
	
	Note: Show examples
	
Now, for these examples I've broken up the partial application from the functional composition into different 
steps.  However, I commonly will mix the partial application and composition together, yielding something like
this.

	episode-006=>(def transformed-parabola
     (comp
      (partial quadratic 1 1 1)
      (partial + 1)))

You can also use point free style to rearrange code like you would with the thread-last macro.  Let's revisit our square fn.

		Note:  Show square-free.

Now, this form is a bit heavy and long to read.  This leads some Clojurians to avoid point-free code.  Others, from a
Haskell background may wonder why Clojure doesn't have better reader support for composition and partial application.
After all, Clojure claims to be a functional language, and as such these tools should be straightforward to code. One 
could experiment with extending the Clojure reader, but I have found a middle ground that I think works well. In my own 
libraries, I have the following two aliases sufficient:

	episode-006=>(def & comp)
	episode-006=>(def p partial)

You'd be surprised how much easier a single character alias makes using a fn.  For example, our square-free fn now looks
like this:

	Note:  Show other square-free.
	
This is nicer than it was, but it is still a bit more work than the threading macro.  However, we are only considering
using our square-free fn on its own.  The true value of point free style isn't apparent until you work in higher order
fns.  Suppose you wanted to map this squaring operation over a list of values

	Note:  Show anon square free

Notice how this fits into the mapping operation anonymously, and everything just works.  This is the type of job where
composition of fns "just works".  map expects a closure, comp produces a closure.  comp expects closures, partial
produces closures.  This is also very easy to grow organically.

	Note:  Show working over a string.
	
Suppose the input is a string, not a set of numbers.  We just add the parsing steps, and it works.  Suppose later we find
out that we need to add a pre-filtering step to this data.  Well, it's comp & partial to the rescue again.

	Note:  Turn into nasty-huge expression
	
You can also see that the indentation level of the form tells us something about the operation.  I use this to quickly
glance and determine what is going on in each stage of the overall process.  I also use it a cue to figure out where one
stage of the process ends, and another begins.

So, that's how I use point free style in Clojure.  It's a different way to organize your code, and works well for certain
problems.  It's not good for everything, especially macro calls & forms with intended side effects.  But, for purely 
functional applications it's handy.  I hope this helps you think about problems from a different angle.  And on that
note, I'm Sean Devlin, and this is Full Disclojure.