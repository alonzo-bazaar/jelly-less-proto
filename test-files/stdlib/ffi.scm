;; utilities for handling results given by tryCall(Static)

(define (good? r) (call r "isGood"))
(define (handle r good-fun bad-fun)
  (if (good? r)
      (good-fun r)
      (bad-fun r)))

(define (error-message r)
  (if (good? r)
      (begin
        ;; all print functions return nil
        ;; but I'm not gonna risk it, even though I'm the one who wrote them
        (println r " is an error result, bruh")
        nil)
      (call r "getErrorMessage")))

(define (result-value r)
  (if (good? r)
      (call r "get")
      (begin
        (println r " is not error result, bruh")
        nil)))

