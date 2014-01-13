(ns sketch.test)

(defmacro is-near [value expected]
  (list '.toBeNear (list 'js/expect value) expected (str value)))
