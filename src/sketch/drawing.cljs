(ns sketch.drawing
  (:require-macros [sketch.macros :as debug]))

(defn draw-body [body ctx]
  (let [pos (:position body)
        x (pos 0)
        y (pos 1)]
    (.fillRect ctx (- x 2) (- y 2) 4 4)))

(defn draw-bodies [bodies ctx]
  (when (seq bodies)
    (draw-body (first bodies) ctx)
    (recur (rest bodies) ctx)))

(defn draw [ctx state width height]
  (let [width (:width state)
        height (:height state)]
    (doto ctx
      (.clearRect 0 0 width height)
      (aset "fillStyle" "#F00")))
  (draw-bodies (:bodies state) ctx))

