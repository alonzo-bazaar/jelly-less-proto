(define (display x)
  (print x))

(define (append-element elt lst)
  (if (null? lst)
      (list elt)
      (cons (car lst) (append-element elt (cdr lst)))))

(define (reverse lst)
  (if (null? lst) (list)
      (append-element (car lst) (reverse (cdr lst)))))

(define (sierpinsky n)
  (let ((lst (list n)))
    (sier-loop lst)))

(define (sier-loop lst)
  (if (not (equal? (car lst) 0))
      (begin
        (print-sier-level lst)
        (sier-loop (update-sier-level (reverse lst))))))

(define (update-sier-level lst)
  ;; update sier level returns the list sorted increasing
  ;; but requires input to be sorted decreasing
  (let ((res (list)))
    (define (tryadd n)
      (if (null? res)
          (set! res (list n))
          (if (equal? (car res) n)
              (set! res (cdr res))
              (set! res (cons n res)))))
    (do ((sublst lst (cdr sublst)))
        ((null? sublst))
      (let ((h (car sublst)))
            (tryadd (+ h 1))
            (tryadd (- h 1))))
    res))

(define (print-sier-level lst)
  ;; requires input to be sorted increasing
  (define (print-sier-level-iter i lst)
    (when (not (null? lst))
      (if (equal? i (car lst))
          (begin
            (display "A")
            (print-sier-level-iter (+ i 1) (cdr lst)))
          (begin
            (display " ")
            (print-sier-level-iter (+ i 1) lst)))))
  (print-sier-level-iter 0 lst)
  (display "\n"))

(sierpinsky 32)
