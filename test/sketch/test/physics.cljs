(ns sketch.test.physics
  (:require [sketch.test.helpers]
            [sketch.test.fixtures :as f]
            [sketch.physics :as p]
            [sketch.vector :as v])
  (:use-macros [purnam.test :only [describe it is is-not]]
               [sketch.test :only [is-within-delta]]))

(describe
  {:doc "polygonal-area"}
  (it "calculates the area of a square"
      (is (p/polygonal-area f/square-offsets) 100))
  (it "calculates the area of a triangle"
      (is (p/polygonal-area f/triangle-offsets) 100))
  (it "calculates the area of a polygon"
      (is (p/polygonal-area f/polygon-offsets) 150)))

(describe
  {:doc "centroid"
   :globals [shifted (map #(v/+ [-3 -2] %) f/square-offsets)
             shifted-more (map #(v/+ [-6 -7] %) f/square-offsets)]}
  (it "calculates the centroid of a square"
      (is (p/centroid f/square-offsets) [5 5]))
  (it "calculates the centroid of a square anywhere"
      (is (p/centroid shifted) [2 3])
      (is (p/centroid shifted-more) [-1 -2]))
  (it "calculates the centroid of a triangle"
      (is (p/centroid f/triangle-offsets) [(/ 10 3) (/ 20 3)])))

(describe
  {:doc "moment-of-area"
   :globals [shifted  [[1 1] [4 1] [4 3] [1 3]]
             centered [[-1.5 -1] [1.5 -1] [1.5 1] [-1.5 1]]]}
  (it "calculates the moment of area of a square centered on the origin"
      (is (p/moment-of-area centered [0 0] 6) 6.5))
  (it "calculates the moment of area of a square centered anywhere"
      (is (p/moment-of-area shifted [2.5 2] 6) 6.5)))

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
