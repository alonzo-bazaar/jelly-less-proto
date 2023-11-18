(define map (lambda (fn lst)
              (if (null lst)
                  nil
                  (cons (fn (car lst)) (map fn (cdr lst))))))

(define filter (lambda (fn lst)
                 (if (null lst)
                     nil
                     (if (fn (car lst))
                         (cons (car lst) (filter fn (cdr lst)))
                         (filter fn (cdr lst))))))
