(ns sketch.physics)

(defn add-vec [a b]
  [(+ (a 0) (b 0)) (+ (a 1) (b 1))])

(defn scale-vec [v s]
  [(* (v 0) s) (* (v 1) s)])

(defn integrate [dt {:keys [position velocity] :as body}]
  (let [new-position (add-vec position (scale-vec velocity dt))]
    (assoc body :position new-position)))
