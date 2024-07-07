(define (trasposta llst)
  (define (valid?) (allEqual? length llst))
  (define (column i) (map (lambda (lst) (nth lst i)) llst))
  (if (valid?)
      (map column (range 0 (length (car llst))))
      (error "invalid input matrix, cannot transpose : TRASPOSTA")))


;; jelly non ha i print più chiari del mondo per ora
(define (printMatrix mat)
  (println "(")
  (dolistFn (lambda (x) (printList x) (newline)) mat)
  (print ")"))
