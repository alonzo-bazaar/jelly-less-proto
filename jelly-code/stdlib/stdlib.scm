#|
emacs calls (load) when trying to load a file from inferior-scheme-mode
defining it like this makes jelly work with inferior-scheme-mode
thus getting a free ide, making development much easier
|#
(define load loadFile)

(loadFile "hof.scm")
(loadFile "list-ops.scm")
(loadFile "print-help.scm")
(loadFile "result-handling.scm")
