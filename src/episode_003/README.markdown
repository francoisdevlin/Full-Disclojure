#Templates

Episode 003

This uses code from the following repository:

	http://github.com/francoisdevlin/devlinsf-clojure-utils

Last episode we ran some experiments to determine how fast transients are.  Something
bothered by about the examples I used.  There's a lot of repetition and duplicated code
in there, it's not very DRY.  This could be solved with a macro, but that seems like more
trouble than it's worth.

Clojure 1.1 has a brand new namespace to address this type of problem, clojure.template.  
Written by Stuart Sierra, it used to be know as clojure.contrib.template.  This namespace
is designed to help reduce boilerplate code without the need for writing a specific 
macro.  In fact, chances are you've been using templates for a while and didn't even know it.

#are

The `are` macro in clojure.test uses templates in order to get things done.  Let's start
writing a simple test, and see how it works.

	episode-003=>(deftest test-first
  	  (are [input result] 
		(= (first input) result)
         [:a :b :c] :a
         "abc" \a
         '(:a :b :c) :a))

This test is designed to make sure that the `first` function behaves properly.  Each
test takes an input and a result, and makes sure that the expression evaluates to true.
Let's run macroexpand-1 and see what happens

	Note:  Run & clean up expansion.
	
Notice that this is a thinly veiled call to do-template.  In fact, the only thing that
changed is that our test expression is wrapped with an is macro.  Let's expand it another
level

	Note:  Run & clean up again.
	
Notice what happened this time.  Our template was repeated three times, with the values
substituted for input and result.  We've just completely dissected the `are` macro.  Pretty
cool.  With this new understanding, let's see if we can create a template to reduce the
amount of code from episode 2. 

#Return to episode 2

In order to see how templates can make your code more concise, let's take a look at
the code from episode 2.

	Note: Open example_002.clj
	
Each of these defn has a very similar structure, with only small variations between them.
First, each one has a `fn-name`.  In this first one it happens to be `vrange`.  The next form
that varies is what I call the `init-form`.  In the case of `vrange`, it is `[]`, and in the case
of `vrange2` is `(transient [])`.  The third form that varies is what I call the `mod-form`, the
part of the code that does the actual modification.  The last part that varies is the return form,
the part that cleans up the final output.

So, our abstract form looks something like this:

	(defn fn-name [n]
      (loop [i 0 c init-form]
       (if (< i n)
         (recur (inc i) mod-form)
         return-form)))

As you can see, there is a list of forms after our template.

What's interesting is that this provides most of the benefit of writing a macro, without the
overhead of splicing.  It's an interesting method of code reuse.

#Is this good practice?

I'm not sure if this is a good or bad practice yet.  Since lisp is centralized around 
building data structures, any facility that makes it easier to do that is worth exploring.
I know there are several cases where I'll write a general function in several variables,
and I need to create specific helper functions of fewer variables.  I think templates would
be a great example for reducing this boilerplate.