(ns sketch.geometry
  (:require [sketch.vector :as v]))

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
