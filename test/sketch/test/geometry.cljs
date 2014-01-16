(ns sketch.test.geometry
  (:require [sketch.test.helpers]
            [sketch.test.fixtures :as f]
            [sketch.geometry :as g]
            [sketch.vector :as v])
  (:use-macros [purnam.test :only [describe it is is-not]]
               [sketch.test :only [is-within-delta]]))

(describe
  {:doc "polygonal-area"}
  (it "calculates the area of a square"
      (is (g/polygonal-area f/square-offsets) 100))
  (it "calculates the area of a triangle"
      (is (g/polygonal-area f/triangle-offsets) 100))
  (it "calculates the area of a polygon"
      (is (g/polygonal-area f/polygon-offsets) 150)))

(describe
  {:doc "centroid"
   :globals [shifted (map #(v/+ [-3 -2] %) f/square-offsets)
             shifted-more (map #(v/+ [-6 -7] %) f/square-offsets)]}
  (it "calculates the centroid of a square"
      (is (g/centroid f/square-offsets) [5 5]))
  (it "calculates the centroid of a square anywhere"
      (is (g/centroid shifted) [2 3])
      (is (g/centroid shifted-more) [-1 -2]))
  (it "calculates the centroid of a triangle"
      (is (g/centroid f/triangle-offsets) [(/ 10 3) (/ 20 3)])))

(describe
  {:doc "moment-of-area"
   :globals [shifted  [[1 1] [4 1] [4 3] [1 3]]
             centered [[-1.5 -1] [1.5 -1] [1.5 1] [-1.5 1]]]}
  (it "calculates the moment of area of a square centered on the origin"
      (is (g/moment-of-area centered [0 0] 6) 6.5))
  (it "calculates the moment of area of a square centered anywhere"
      (is (g/moment-of-area shifted [2.5 2] 6) 6.5)))
