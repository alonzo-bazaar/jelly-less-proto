(define (map fn lst)
  (if (null lst)
      nil
      (cons (fn (car lst)) (map fn (cdr lst)))))

(define (filter fn lst)
  (if (null lst)
      nil
      (if (fn (car lst))
          (cons (car lst) (filter fn (cdr lst)))
          (filter fn (cdr lst)))))

(define (append a b)
  (if (null a) b
      (cons (car a) (append (cdr a) b))))

(define (reduce fn lst neutral)
  (if (null lst) neutral
      (fn (car lst) (reduce fn (cdr lst) neutral))))

(define (flatten lst)
  (reduce append lst nil))

(define (flatmap fn lst)
  (flatten (map fn lst)))
  
