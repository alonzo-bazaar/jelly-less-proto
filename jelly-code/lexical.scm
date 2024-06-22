(define withdraw
  (let ((balance 100))
    (lambda (amount)
      (if (> amount balance)
          "insufficient funds"
          (begin
            (set! balance (- balance amount))
            balance)))))

(define make-withdraw (lambda (balance)
                        (lambda (amount)
                          (if (>= balance amount)
                              (begin (set! balance (- balance amount))
                                     balance)
                              "insufficient funds"))))
