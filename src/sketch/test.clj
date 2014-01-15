(ns sketch.test)

(defmacro is-near [value expected]
  (list '.toBeNear (list 'js/expect value) expected (str value)))

(defmacro is-within-delta [value expected]
  (list '.toBeCloseTo (list 'js/expect value) expected 3))
