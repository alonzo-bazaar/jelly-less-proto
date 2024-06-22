(define (reverse lst)
  (do ((iter lst (cdr iter))
        (acc nil (cons (car iter) acc)))
    ((null? iter) acc)))

(define (append lst1 lst2)
  (if (null? lst1)
      lst2
      (cons (car lst1) (append (cdr lst1) lst2))))

(define (flatten lst)
  (reduce append lst nil))

(define (flatmap fn lst)
  (flatten (map fn lst)))
