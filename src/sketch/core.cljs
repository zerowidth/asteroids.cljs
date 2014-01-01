(ns sketch.core
  (:require [clojure.browser.dom :as dom])
  (:require-macros [sketch.macros :as debug]))

(defn init []
  (let [canvas (dom/get-element "sketch")
        ctx    (.getContext canvas "2d")]
    (dom/set-properties canvas {"width" "400px" "height" "400px"})
    (doto ctx
      (aset "fillStyle" "#F00")
      (.fillRect 100 100 200 100))))

(init)
