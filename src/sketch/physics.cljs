(ns sketch.physics
  (:require [sketch.vector :as v]
            [sketch.rotation :as r]))

(defn vertex-pairs [vertices]
  (partition 2 1 (concat vertices (list (first vertices)))))

(defn polygonal-area
  "Use the surveyor's formula for calculating the signed area of a polygon"
  [vertices]
  (let [pairs (vertex-pairs vertices)]
    (/ (reduce + (map #(apply v/cross-product %) pairs)) 2)))

(defn centroid [vertices]
  "Calculate the centroid of the polygon with the given vertices"
  (let [pairs (vertex-pairs vertices)
        area (polygonal-area vertices)
        cx-part (fn [ [[x0 y0] [x1 y1]]] (* (+ x0 x1) (- (* x0 y1) (* x1 y0))))
        cy-part (fn [ [[x0 y0] [x1 y1]]] (* (+ y0 y1) (- (* x0 y1) (* x1 y0))))
        cx (/ (reduce + (map cx-part pairs)) (* 6 area))
        cy (/ (reduce + (map cy-part pairs)) (* 6 area))]
    [cx cy]))

(defn moment-of-area [vertices centroid area]
  "Calculate the moment of area given the vertices of a polygon and the
   precomputed centroid and area"
  (let [pairs (vertex-pairs vertices)
        ix-part (fn [ [[x0 y0] [x1 y1]] ]
                  (* (+ (* y0 y0) (* y0 y1) (* y1 y1))
                     (v/cross-product [x0 y0] [x1 y1])))
        iy-part (fn [ [[x0 y0] [x1 y1]] ]
                  (* (+ (* x0 x0) (* x0 x1) (* x1 x1))
                     (v/cross-product [x0 y0] [x1 y1])))
        ix (/ (reduce + (map ix-part pairs)) 12)
        iy (/ (reduce + (map iy-part pairs)) 12)
        [cx cy] centroid
        ; # parallel axis theorem to recenter moment around centroid:
        ix (- ix (* area cx cx))
        iy (- iy (* area cy cy))]
    (+ ix iy)))

(defn physical-properties
  "Calculate the physical properties (area, mass, angular moment) of a body"
  [{:keys [offsets density] :as body}]
  (let [signed-area (polygonal-area offsets)
        c (centroid offsets)
        mass (* density signed-area)
        moment (* (/ mass signed-area) (moment-of-area offsets c signed-area))
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
