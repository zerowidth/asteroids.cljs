(ns sketch.test.world
  (:require [sketch.test.helpers]
            [sketch.test.fixtures :as f]
            [sketch.physics :as p]
            [sketch.vector :as v]
            [sketch.rotation :as r]
            [sketch.world :as w])
  (:use-macros [purnam.test :only [describe it is is-not]]
               [sketch.test :only [is-near]]))

(describe
  {:doc "recenter-on-centroid"}
  (describe
    {:doc "with a simple square"
     :globals [square (p/physical-properties f/test-square)
             centered (w/recenter-on-centroid square)]}
    (it "updates the position to match the calculated centroid"
        (is (:centroid square) [5 5])
        (is (:position square) [0 0])
        (is (:position centered) [5 5]))
    (it "updates the offsets around the calculated centroid"
        (is (:offsets centered) [[-5 -5] [5 -5] [5 5] [-5 5]]))
    )
  (describe
    {:doc "with a rotated square"
     :globals [square (-> f/test-square
                          (assoc :orientation (r/from-angle Math/PI))
                          (p/physical-properties))
               centered (w/recenter-on-centroid square)]}
    (it "calculates centroid relative to position only, not orientation"
        (is (:centroid square) [5 5]))
    (it "updates the position to match the calculated centroid using orientation"
        (is-near (:position centered) [-5 -5]))
    (it "updates the offsets around the calculated centroid only"
        (is (:offsets centered) [[-5 -5] [5 -5] [5 5] [-5 5]]))))
