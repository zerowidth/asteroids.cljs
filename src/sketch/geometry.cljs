(ns sketch.geometry
  (:require [sketch.vector :as v]))

(defn aabb
  "Returns an axis-aligned bounding box for a given list of vertices"
  [vertices]
  [[(apply min (map first vertices)) (apply min (map last vertices))]
   [(apply max (map first vertices)) (apply max (map last vertices))]]
  )

(defn aabb-center
  "Returns the center of an axis-aligned bounding box for a list of vertices"
  [vertices]
  (let [[[x0 y0] [x1 y1]] (aabb vertices)]
    [(+ x0 (/ (- x1 x0) 2))
     (+ y0 (/ (- y1 y0) 2))]))

(defn vertex-pairs
  "Given a list of vertices, return all sequential pairs"
  [vertices]
  (partition 2 1 (concat vertices (list (first vertices)))))

(defn vertex-pair-to-vector
  "Given a vertex pair, return a vector describing a->b"
  [a b]
  (v/- b a))

(defn polygonal-area
  "Use the surveyor's formula for calculating the signed area of a polygon"
  [vertices]
  (let [pairs (vertex-pairs vertices)]
    (/ (reduce + (map #(apply v/cross-product %) pairs)) 2)))

(defn centroid
  "Calculate the centroid of the polygon with the given vertices"
  [vertices]
  (let [pairs (vertex-pairs vertices)
        area (polygonal-area vertices)
        cx-part (fn [ [[x0 y0] [x1 y1]]] (* (+ x0 x1) (- (* x0 y1) (* x1 y0))))
        cy-part (fn [ [[x0 y0] [x1 y1]]] (* (+ y0 y1) (- (* x0 y1) (* x1 y0))))
        cx (/ (reduce + (map cx-part pairs)) (* 6 area))
        cy (/ (reduce + (map cy-part pairs)) (* 6 area))]
    [cx cy]))

(defn moment-of-area
  "Calculate the moment of area given the vertices of a polygon and the
   precomputed centroid and area"
  [vertices centroid area]
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

(defn project-polygon-on-axis
  "Project a polygon onto a given axis. Returns a pair of points on the axis."
  [vertices axis]
  (let [projections (map #(v/dot-product % axis) vertices)]
    [ (apply min projections) (apply max projections) ]))

(defn interval-overlap
  "Calculate by how much two intervals overlap.

   --a--
          --b-- => 0

   --a--
      --b--     => 2

      --a--
   --b--        => 2

          --a--
   --b--        => 0

   ----a----
     --b--      => 5

     --a--
   ----b----    => 5

   Returns an integer. 0 means no overlap.
   "
  [[a0 a1] [b0 b1]]
  (if (or (> b0 a1) (< b1 a0))
    0
    (- (min a1 b1) (max a0 b0))))

(defn overlap-along-axis
  "Given two polygons and an axis, determine how much they overlap."
  [a b axis]
  (interval-overlap
    (project-polygon-on-axis a axis)
    (project-polygon-on-axis b axis)))

(defn separating-axes
  "find the separating axes given two polygons."
  [a b]
  (->> (concat
         (vertex-pairs a)
         (vertex-pairs b))
       (map #(apply vertex-pair-to-vector %))
       (map v/perpendicular-normal)))

(defn normalize-direction
  "Modifies an axis so that it points from polygon a -> polygon as calculated
   based on the centers of the polygons' axis-aligned bounding boxes."
  [axis a b]
  (let [center-a (aabb-center (aabb a))
        center-b (aabb-center (aabb b))
        direction (v/- center-b center-a)]
    (if (> (v/dot-product direction axis) 0)
      axis
      (v/- axis))))

(defn minimum-separation-axis
  "Calculate the minimum separating axis between two polygons.

   Returns nil if not overlapping, or a pair of:
   - A vector normal of the minimum separation axis of the two polygons.
   - The magnitude of the separation along the axis.
   "
  [a b]
  (let [axes (separating-axes a b)
        overlaps (map #(overlap-along-axis a b %) axes)
        pairs (map vector axes overlaps)
        min-overlap (apply min-key last pairs)]
    (if (= 0 (last min-overlap))
      nil
      [(normalize-direction (first min-overlap) a b) (last min-overlap)])))
