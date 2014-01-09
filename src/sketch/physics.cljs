(ns sketch.physics
  (:require [sketch.vector :as v]
            [sketch.rotation :as r]))

(defn integrate [dt {:keys [position velocity
                            orientation angular-velocity] :as body}]
  (let [new-position (v/+ position (v/* velocity dt))
        new-orientation (r/add-angle orientation (* angular-velocity dt))]
    (assoc body :position new-position
           :orientation new-orientation)))

