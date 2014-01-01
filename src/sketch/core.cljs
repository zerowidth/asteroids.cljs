(ns sketch.core
  (:require [clojure.browser.dom :as dom])
  (:require-macros [sketch.macros :as debug]))

(def canvas-width 400)
(def canvas-height 400)

(defn setup [canvas ctx]
  (let [width (str canvas-width "px")
        height (str canvas-height "px")]
    (dom/set-properties canvas {"width" width "height" height}))
  ; invert the y coordinates so y starts at bottom
  (.setTransform ctx 1 0 0 -1 0 canvas-height))

(defn update [dt])

(defn draw [ctx]
  (doto ctx
    (aset "fillStyle" "#F00")
    (.fillRect 100 100 200 100)))

(defn init [canvas-id]
  (let [canvas (dom/get-element canvas-id)
        ctx    (.getContext canvas "2d")]
    (setup canvas ctx)
    (letfn [(frame [dt]
              (draw ctx)
              (js/requestAnimationFrame frame))]
      (js/requestAnimationFrame frame))))

(init "sketch")
