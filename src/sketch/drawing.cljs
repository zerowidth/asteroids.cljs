(ns sketch.drawing
  (:require [sketch.vector :as v])
  (:require-macros [sketch.macros :as debug]))

(defn draw-polygon [ctx points]
  (let [moveTo (.-moveTo ctx)
        lineTo (.-lineTo ctx)
        line (fn [p] (.apply lineTo ctx (into-array p)))]
    (.beginPath ctx)
    (.apply moveTo ctx (into-array (last points)))
    (doseq [[x y] points]
      (.lineTo ctx x y))
    (.fill ctx)))

(defn draw-body [ctx {:keys [position orientation offsets]}]
  (let [points (map #(v/transform % position orientation) offsets)]
    (draw-polygon ctx points)))

(defn draw-bodies [ctx bodies]
  (when (seq bodies)
    (draw-body ctx (first bodies))
    (recur ctx (rest bodies))))

(defn draw [ctx state width height]
  (let [width (:width state)
        height (:height state)]
    (doto ctx
      (.clearRect 0 0 width height)
      (aset "fillStyle" "#F00")))
  (draw-bodies ctx (:bodies state)))
