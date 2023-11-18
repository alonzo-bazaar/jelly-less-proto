(define fizzbuzz (lambda (n)
                   (define divides (lambda (x y)
                                     (= 0 (mod x y))))
                   (if (divides n 15)
                       "FizzBuzz"
                       (if (divides n 3)
                           "Fizz"
                           (if (divides n 5)
                               "Buzz"
                               n)))))

(let ((divides 0))
  (while (< divides 30)
         (print (fizzbuzz divides))
         (set divides (+ 1 divides))))
