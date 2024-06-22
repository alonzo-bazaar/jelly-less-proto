(define (reduce fn lst neutral)
  (if (null? lst) neutral
      (fn (car lst) (reduce fn (cdr lst) neutral))))

(define (dolistFn fn lst)
  (do ((l lst (cdr l)))
      ((null? l) nil)
    (fn (car l))))

(define (map fn lst)
  (do ((iter lst (cdr iter))
        (acc nil (cons (fn (car iter)) acc)))
    ((null? iter) (reverse acc))))
