(define loadModule
  #|
  traduzione lisp dell'idioma C
  #ifndef
  #define
  /* ... */
  #endif

  come meccanismo temporaneo per evitare di leggere due volte un file di codice
  |#
  (let (loadedFiles ())
    (lambda (fileName)
      (if (contains? loadedFiles fileName)
          (begin
            (println fileName " already loaded, loaded files are : ")
            (printList loadedFiles))
        (loadFile fileName)))))
