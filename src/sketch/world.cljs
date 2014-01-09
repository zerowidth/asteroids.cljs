(ns sketch.world
  (:require [sketch.physics :as physics]
            [sketch.rotation :as rot])
  (:require-macros [sketch.macros :as debug]))

(def default-body
  {
   ; where it is
   :position [0 0]
   :orientation (rot/from-angle 0)

   ; where it's going
   :velocity [0 0]
   :angular-velocity 0

   ; what shape it is (offsets from position), default to a square
   :offsets [[0 0] [10 0] [10 10] [0 10]]

   ; what it looks like
   :color "#CCC"
   })

(def test-body {:position [100 0] :velocity [100 50]})

(defn test-square
  "create a test square centered around x and y and of size size"
  ([x y size]
   (test-square x y size {}))
  ([x y size properties]
   (let [hw (/ size 2) ; half-width
         offsets [[(- hw) (- hw)] [hw (- hw)] [hw hw] [(- hw) hw]] ]
     (merge default-body properties {:position [x y] :offsets offsets}))))

(defn constrain [n size]
  (loop [value n]
    (cond (< value 0) (recur (+ value size))
          (> value size) (recur (- value size))
          :else value)))

(defn wrap-position [width height body]
  (let [pos (:position body)
        x (constrain (pos 0) width)
        y (constrain (pos 1) height)]
    (assoc body :position [x y])))


(defn add-body [state properties]
  (let [new-body (merge default-body properties)]
    (swap! state (fn [state]
                   (assoc state :bodies (conj (:bodies state) new-body))))))

(defn setup [state width height]
  (reset! state {:bodies [] :particles [] :width width :height height})
  (add-body state test-body))

(defn update [state dt]
  (let [dt (/ dt 1000)
        width (:width state)
        height (:height state)
        bodies (->>
                 (:bodies state)
                 (map (partial physics/integrate dt))
                 (map (partial wrap-position width height)))]
    (assoc state
           :bodies bodies)))
