#Templates

Episode 003

This uses code from the following repository:

	http://github.com/francoisdevlin/devlinsf-clojure-utils

This episode covers a new namespace in version 1.1, clojure.template.  This namespace
is designed to help reduce boilerplate code without the need for writing a specific 
macro.  It's used in clojure.test to write the `are` macro.  This can be very helpful
with the bottom up style of development common in a lisp.

In order to see how templates can make your code more concise, let's take a look at
the code from episode 2.

	Note: Open example_002.clj
	
Each of these defn has a very similar structure, with only small variations between them.
First, each one has a `fn-name`.  In this first one it happens to be `vrange`.  The next form
that varies is what I call the `init-form`.  In the case of `vrange`, it is `[]`, and in the case
of `vrange2` is `(transient [])`.  The third form that varies is what I call the `mod-form`, the
part of the code that does the actual modification.


#Is this good practice?

I'm not sure if this is a good or bad practice yet.  Since lisp is centralized around 
building data structures, and facility that makes it easier to do is worth exploring.