(ns sketch.world
  (:require [sketch.physics :as physics]
            [sketch.vector :as v]
            [sketch.rotation :as rot]))

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
   :color "#F00"
   })

(defn test-square
  "create a test square centered around x and y and of size size"
  ([x y size]
   (test-square x y size {}))
  ([x y size properties]
   (let [hw (/ size 2) ; half-width
         offsets [[(- hw) (- hw)] [hw (- hw)] [hw hw] [(- hw) hw]] ]
     (merge properties {:position [x y] :offsets offsets}))))

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
  (let [new-body (merge default-body properties)
        bodies (:bodies state)]
    (assoc state :bodies (conj bodies new-body))))

(defn recenter-on-centroid
  "reposition a body around its computed centroid"
  [{:keys [position orientation offsets centroid] :as body}]
  (let [; transformed centroid offset
        offset (v/rotate centroid orientation)
        ; position change needs transformed offset
        position (v/+ position offset)
        ; but the offsets themselves do not.
        offsets (map #(v/- % centroid) offsets)]
  (assoc body :position position :offsets offsets)))

(defn setup [state]
  (-> state
      (assoc :bodies [] :particles [])
      (add-body (test-square 100 100 10
                             {:velocity [10 20]
                              :color "#08F"}))
      (add-body (test-square 100 0 100
                             {:velocity [100 50]}))))

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
