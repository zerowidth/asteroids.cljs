(ns sketch.vector)

(defn + [[ax ay] [bx by]]
  [(cljs.core/+ ax bx) (cljs.core/+ ay by)])

(defn * [[x y] scale]
  [(cljs.core/* x scale) (cljs.core/* y scale)])

(defn transform
  " | a b c |   | x |   | ax + by + c |
    | e f g | * | y | = | ex + fy + g |
    | 0 0 1     | 1 |   | 1           |

    translation is [dx, dy]
    orientation is [cos(theta), sin(theta)]

    using this rotation / translation matrix:
    | cos(theta) -sin(theta) dx |
    | sin(theta)  cos(theta) dy |
  "
  [v translation orientation]
  (let [[x y] v
        [dx dy] translation
        [r i] orientation]
    [(cljs.core/+ (cljs.core/* r x) (cljs.core/* i (- y)) dx)
     (cljs.core/+ (cljs.core/* i x) (cljs.core/* r y) dy)]))
