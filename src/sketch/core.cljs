(ns sketch.core
  (:require [clojure.browser.dom :as dom]
            [goog.dom :as gdom]
            [goog.style :as style]
            [goog.events :as events]
            [sketch.world :as world]
            [sketch.drawing :as drawing]))

(def state (atom {}))
(def running (atom true))
(def paused (atom false))
(def clock (atom 0))

(declare frame)

(defn request-next-frame []
  (js/requestAnimationFrame frame))

(defn frame [timestamp]
  (let [elapsed (- timestamp @clock)]
    (when (and @running (not @paused))
      (swap! state world/update elapsed)
      (drawing/draw @state)))
  (reset! clock timestamp)
  (request-next-frame))

(defn keydown [event]
  (condp = (.-keyCode event)
    32 (swap! paused not)
    :ignored))

(defn window-onblur [event]
  (compare-and-set! running true false))

(defn window-onfocus [event]
  (if (compare-and-set! running false true)
    (request-next-frame)))

(defn setup [state]
  (let [canvas (dom/get-element "sketch")
        ctx (.getContext canvas "2d")
        ; 31 is height of header p tag, so 10px on bottom/sides:
        canvas-width (- (.-width (gdom/getViewportSize)) 20)
        canvas-height (- (.-height (gdom/getViewportSize)) 41)
        width  (str canvas-width  "px")
        height (str canvas-height "px")
        container (dom/get-element "container")
        header (dom/get-element "header")]
    (dom/set-properties canvas {"width" width "height" height})
    (style/setSize container canvas-width canvas-height)
    (style/setWidth header canvas-width)
    (events/listen js/document "keydown" keydown)
    (events/listen js/window "blur" window-onblur)
    (events/listen js/window "focus" window-onfocus)
    ; invert the y coordinates so y starts at bottom
    (.setTransform ctx 1 0 0 -1 0 canvas-height)
    (assoc state :ctx ctx :width canvas-width :height canvas-height)))

(defn ^:export init []
  (enable-console-print!)
  (reset! state (-> {} setup world/setup))
  (request-next-frame))
