(define (all lst)
  (if (null lst)
      t
      (if (car lst)
          (all (cdr lst))
          nil)))

(define (dolist-lambda lst fn)
  (let ((x lst))
    (while x
           (fn (car x))
           (set! x (cdr x)))))

(define (update-sier-row indices-row)
  (let ((res nil))
    (define (tryadd i)
      (if (all (list res
                     (= (car res) i)))
          (set! res (cdr res))
          (set! res (cons i res))))
    (dolist-lambda indices
                   (lambda (ind)
                     (tryadd (- ind 1) res)
                     (tryadd (+ ind 1) res)))))

(define (print-indices indices)
  (let ((i 0)
        (remaining indices))
    (while remaining
           (print i remaining)
           (if (= i (car remaining))
               (begin (print "A")
                      (set! remaining (cdr remaining)))
               (print " "))
           (set! i (+ i 1)))))

(define (print-sier n)
  (let ((indices (list n)))
    (while (not (= (car indices) 0))
           (print-indices indices)
           (set! indices (update-sier-row indices)))))
