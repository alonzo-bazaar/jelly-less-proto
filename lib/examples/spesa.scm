;; prodotto definito come struct
(define (make-product name price amount)
  (list name price amount))

(define (product-name product) (car product))
(define (product-price product) (car (cdr product)))
(define (product-amount product) (car (cdr (cdr product))))

(define (list-cost lst)
  (define (product-total product)
    (* (product-price product) (product-amount product)))
  (reduce + (map product-total lst) 0))

(list-cost (list
            (make-product "fusilli" 1.50 2)
            (make-product "zucchine" 0.80 10)
            (make-product "latte" 2.00 2)
            (make-product "ventilatore" 25.00 1)))
