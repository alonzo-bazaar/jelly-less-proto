;; print
(define (printSepList sep lst)
  (do ((l lst (cdr l)))
      ((null? l) nil)
    (display (car l))
    (unless (null? (cdr l))
      (display sep))))

(define (printSep sep &rest args)
  (printSepLst sep args))

(define (print &rest args)
  (printSepList " " args))

(define (displayty arg separator)
  (display arg)
  (display separator)
  (display (call (call arg "") "getCanonicalName")))

(define (printtySepList sep lst)
  (do ((l lst (cdr l)))
      ((null? l) nil)
    (displayty (car l) #\:)
    (unless (null? (cdr l))
      (display sep))))

(define (printtySep sep &rest args)
  (do ((a args (cdr a)))
      ((null? a) nil)
    (displayty (car a))
    (unless (null? (cdr a))
      (display sep))))

(define (printty &rest args)
  (printtySepList " " args))

(define (println &rest args)
  (dolist-fn display args)
  (terpri))

(define (terpri)
  (dispalay #\Newline))

(define (printList lst)
  (display "(")
  (printSepList " " lst)
  (display ")"))
