(ns sketch.test.physics
  (:require [purnam.cljs]
            [sketch.physics :as p])
  (:use-macros [purnam.test :only [init describe it is is-not]]))

(init) ; initialize jasmine

(def triangle-offsets [[0 0] [10 0] [0 20]])
(def square-offsets [[0 0] [10 0] [10 10] [0 10]])
(def polygon-offsets [[0 0] [10 0] [10 10] [0 20]])
(def test-square
  {:density 2
   :offsets square-offsets})

(describe
  {:doc "polygonal-area"}
  (it "calculates the area of a square"
      (is (p/polygonal-area square-offsets) 100))
  (it "calculates the area of a triangle"
      (is (p/polygonal-area triangle-offsets) 100))
  (it "calculates the area of a polygon"
      (is (p/polygonal-area polygon-offsets) 150)))

(describe
  {:doc "physical-properties with a square"
   :globals [square (p/physical-properties test-square)]}
  (it "calculates the area"
      (is (:area square) 100))
  (it "sets the inverse mass based on area and density"
      (is (:inverse-mass square) 0.005))
  (it "does not override inverse mass if set"
      (is
        (:inverse-mass (p/physical-properties
                         (assoc test-square :inverse-mass 0.1)))
        0.1)))
