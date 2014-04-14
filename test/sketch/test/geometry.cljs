(ns sketch.test.geometry
  (:require [sketch.test.helpers]
            [sketch.test.fixtures :as f]
            [sketch.geometry :as g]
            [sketch.vector :as v]
            [sketch.rotation :as r])
  (:use-macros [purnam.test :only [describe it is is-not]]
               [sketch.test :only [is-within-delta]]))

(defn test-square
  ([position half-width] (test-square position half-width 0))
  ([position half-width angle]
   (let [a (- half-width)
         b half-width
         orientation (r/from-angle angle)]
     (map #(v/transform % position orientation) [[a a] [b a] [b b] [a b]]))))

(describe
  {:doc "aabb"}
  (it "calculates the axis-aligned bounding box of a shape"
      (is (g/aabb (test-square [0 0] 1))
          [[-1 -1] [1 1]])))

(describe
  {:doc "aabb-center"}
  (it "calculates the center of a given axis-aligned bounding box"
      (is (g/aabb-center [[-1 -1] [2 2]])
          [0.5 0.5])))

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

(describe
  {:doc "project-polygon-on-axis"}
  (it "returns a range for a polygon projected on an axis"
      (is (g/project-polygon-on-axis
            (test-square [0 0] 1) [1 0]) [-1 1])
      (is (g/project-polygon-on-axis
            (test-square [0.5 0.5] 0.5) [1 0]) [0 1])))

(describe
  {:doc "interval-overlap"}
  (it "calculates how much two intervals overlap"
      (is (g/interval-overlap [0 1] [2 3]) 0)
      (is (g/interval-overlap [0 2] [1 3]) 1)
      (is (g/interval-overlap [0 2] [-1 1]) 1)
      )
  (it "handles overlap when one interval is contained within another"
      (is (g/interval-overlap [0 10] [2 4]) 2)
      (is (g/interval-overlap [1 2] [0 5]) 1)))

(describe
  {:doc "normalize-direction"}
  (it "returns an axis if it points from a shape a -> b"
      (is (g/normalize-direction
            [1 0]
            (test-square [0 0] 1)
            (test-square [0.5 0] 0.75))
          [1 0]))
  (it "returns the inverse of an axis if it points from b -> a"
      (is (g/normalize-direction
            [-1 0]
            (test-square [0 0] 1)
            (test-square [0.5 0] 0.75)) [1 0])))

(describe
  {:doc "minimum-separation-axis"}
  (it "returns nil for shapes that aren't overlapping"
      (is (g/minimum-separation-axis
            (test-square [0 0] 0.5)
            (test-square [0 2] 0.5)) nil))
  (it "returns a vector and magnitude pair when two squares overlap"
      (is (g/minimum-separation-axis
            (test-square [0 0] 1)
            (test-square [0 1] 0.5))
          [[0 1] 0.5])))
