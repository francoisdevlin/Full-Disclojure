#Abstraction Grafting

One of Clojure's greatest strengths is the sequence abstraction.  Being able to use the
same functions on vectors, maps, sets, and more makes it much easier to learn the language.
This works well because Clojure's data structures implement the `Seqable` interface, and each
sequence fn has an initial call the seq method.  This is a classic use of a Java interface, right?

Wrong.  Any of you watching that have being using Clojure for a while know that my statements
are incomplete.  The sequence abstraction also extends to String, native arrays, and several of
the collections in the java.utils package.  `seq` works on things that do not implement seqable,
and this makes interacting with legacy code much easier.

Which brings me to what I'd like to talk about today.  There are a lot of objects in Java that are
used in a related way, but there is no common interface to unite them.  Sometimes you can use double
dispatch to get around things, but that means there's a lot of ceremonious glue code that needs to 
be written to make things work. Other times you're just plain stuck. It's everything people hate 
about Java.

Let's take a look at something that is a real life pain point in the java world.  Anyone that's worked
with the Java time system knows it is difficult to use.  There isn't a unifying interface, and converting
between types is a lot of repetitive work.  Clojure provides a way out of this easily and elegantly.  
Let's start by taking a look at the various ways Java represents time.

* java.util.Date
* java.util.GregorianCalendar
* java.lang.Long
* javax.sql.Timestamp

The first thing we have to do is determine what our common time type is.  If you dig down into the
details of each of these, it's eventually possible to convert each of these to a Long.  We'll start by
writing a multimethod to transform each of these to a long.

	Note:  Show to-ms
	
There are a few corner cases to handle up front, empty & nil.  Empty returns the ms counter this instant,
and nil passes a nil object through.  The next case on the list is when a long is passed.  Again, this 
value is passed straight through.  Now we come to our first real case, Date

	Note:  Show Date case