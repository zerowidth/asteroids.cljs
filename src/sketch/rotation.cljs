(ns sketch.rotation)

(defn from-angle [angle]
  [(Math/cos angle) (Math/sin angle)])

(defn + [[a b] [c d]]
  [(- (* a c) (* b d)) (cljs.core/+ (* a d) (* c b))])

(defn add-angle [rotation angle]
  (+ rotation (from-angle angle)))
