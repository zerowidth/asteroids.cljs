(ns sketch.test.fixtures)

(def triangle-offsets [[0 0] [10 0] [0 20]])
(def square-offsets [[0 0] [10 0] [10 10] [0 10]])
(def polygon-offsets [[0 0] [10 0] [10 10] [0 20]])
(def test-square
  {:position [0 0]
   :orientation [1 0]
   :density 2
   :offsets square-offsets})

