(ns sketch.physics
  (:require [sketch.vector :as v]
            [sketch.rotation :as r]))

(defn polygonal-area
  "Use the shoelace (surveyor's) formula for calculating the signed area of a polygon"
  [vertices]
  (let [pairs (partition 2 1 (concat vertices (list (first vertices))))
        signed-area (fn [ [[x0 y0] [x1 y1]] ] (- (* x0 y1) (* x1 y0)))]
    (/ (reduce + (map signed-area pairs)) 2)))

(defn physical-properties
  "Calculate the physical properties (area, mass, angular moment) of a body."
  [{:keys [offsets density] :as body}]
  (let [area (polygonal-area offsets)
        mass (* density area)
        inv-mass (if (> mass 0) (/ mass) 0)]
    (merge-with (fn [a b] a)
                body
                {:area area :inverse-mass inv-mass})))

(defn integrate
  "update the position and orientation of a body given a time delta dt"
  ; TODO handle acceleration too
  [dt {:keys [position velocity
              orientation angular-velocity] :as body}]
  (let [new-position (v/+ position (v/* velocity dt))
        new-orientation (r/add-angle orientation (* angular-velocity dt))]
    (assoc body
           :position new-position
           :orientation new-orientation)))
