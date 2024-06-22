;; result handling
(define (goodResult? result)
  (call result "isGood"))

(define (resultGet result)
  (call result "get"))

(define (handle r good-fun bad-fun)
  (if (goodResult? r)
      (good-fun r)
      (bad-fun r)))

(define (getErrorMessage r)
  (if (good? r)
      (begin
        (println r " is an error result, bruh")
        #f)
      (call r "getErrorMessage")))
