(ns sketch.drawing
  (:require [sketch.vector :as v]))

(defn axis-offsets [points idx min max]
  (cond-> [0]
    (some #(< (% idx) min) points) (conj  1)
    (some #(> (% idx) max) points) (conj -1)))

(defn draw-polygon [ctx points]
  (.beginPath ctx)
  (.apply (.-moveTo ctx) ctx (into-array (last points)))
  (doseq [[x y] points]
    (.lineTo ctx x y))
  (.fill ctx))

(defn draw-offset-polygon [ctx points offset]
  (draw-polygon ctx (map #(v/+ % offset) points)))

(defn draw-wrapped-polygon [ctx points width height]
  (doseq [x-offset (axis-offsets points 0 0 width)
          y-offset (axis-offsets points 1 0 height)]
    (if (and (= 0 x-offset) (= 0 y-offset))
      (draw-polygon ctx points)
      (let [offset [(* width x-offset) (* height y-offset)]]
        (draw-offset-polygon ctx points offset)))))

(defn draw-body [ctx {:keys [position orientation offsets]} width height]
  (let [points (map #(v/transform % position orientation) offsets)]
    (draw-wrapped-polygon ctx points width height)))

(defn draw-bodies [ctx bodies width height]
  (let [by-color (group-by :color bodies)]
    (doseq [[color group] by-color
            body group]
      (aset ctx "fillStyle" color)
      (draw-body ctx body width height))))

(defn draw [{:keys [width height ctx bodies]}]
  (doto ctx
    (.clearRect 0 0 width height))
  (draw-bodies ctx bodies width height))
