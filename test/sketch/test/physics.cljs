(ns sketch.test.physics
  (:require [sketch.test.helpers]
            [sketch.test.fixtures :as f]
            [sketch.physics :as p]
            [sketch.vector :as v])
  (:use-macros [purnam.test :only [describe it is is-not]]
               [sketch.test :only [is-within-delta]]))

(describe
  {:doc "physical-properties with a square"
   :globals [square (p/physical-properties f/test-square)]}
  (it "calculates the area"
      (is (:area square) 100))
  (it "sets the inverse mass based on area and density"
      (is (:inverse-mass square) 0.005))
  (it "does not override inverse mass if set"
      (is
        (:inverse-mass (p/physical-properties
                         (assoc f/test-square :inverse-mass 0.1)))
        0.1))
  (it "sets the centroid in terms of the offsets"
      (is (:centroid square) [5 5]))
  (it "sets the inverse moment of area"
      ; bh/12 * (b^2 + h^2) = 100*200/12 => 12/(100*200)
      (is-within-delta (:inverse-moment square) 0.0005999))
  (it "takes mass into account"
      (is-within-delta
        (:inverse-moment (p/physical-properties
                         (assoc f/test-square :density 0.01)))
        0.05999))
  (it "does not override inverse moment if set"
      (is
        (:inverse-moment (p/physical-properties
                           (assoc f/test-square :inverse-moment 0.1)))
        0.1)))
