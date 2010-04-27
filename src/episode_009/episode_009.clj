(ns episode-009
  (:use lib.sfd.debug))

(def code-string "(map inc (list 1 2 3 4))")

(= (read-string code-string)
   '(map inc (list 1 2 3 4)))

(map inc (list 1 2 3 4))

(-> c (* 1.8) (+ 32))


(defprotocol ifn2
  (invoke
   [arg1]
   [arg1 arg2]
   [arg1 arg2 arg3]
   [arg1 arg2 arg3 arg4]
   [arg1 arg2 arg3 arg4 arg5]
   [arg1 arg2 arg3 arg4 arg5 arg6]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18 arg19]
   [arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18 arg19 arg20])
  (apply-to [arglist]))
  