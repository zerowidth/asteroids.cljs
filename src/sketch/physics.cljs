(ns sketch.physics
  (:require [sketch.vector :as v]
            [sketch.rotation :as r]
            [sketch.geometry :as g]))

(defn physical-properties
  "Calculate the physical properties (area, mass, angular moment) of a body"
  [{:keys [offsets density] :as body}]
  (let [signed-area (g/polygonal-area offsets)
        c (g/centroid offsets)
        mass (* density signed-area)
        moment (* (/ mass signed-area) (g/moment-of-area offsets c signed-area))
        inv-mass (if (> mass 0) (/ mass) 0)
        inv-moment (if (> moment 0) (/ moment) 0)]
    (merge-with (fn [a b] a) body
                {:area (Math/abs signed-area)
                 :inverse-mass inv-mass
                 :inverse-moment inv-moment
                 :centroid c})))

(defn integrate
  "Update the position and orientation of a body given a time delta dt"
  ; TODO handle acceleration too
  [dt {:keys [position velocity
              orientation angular-velocity] :as body}]
  (let [new-position (v/+ position (v/* velocity dt))
        new-orientation (r/add-angle orientation (* angular-velocity dt))]
    (assoc body
           :position new-position
           :orientation new-orientation)))
