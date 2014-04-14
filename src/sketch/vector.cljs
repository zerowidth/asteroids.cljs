(ns sketch.vector
  (:refer-clojure :exclude [+ - *]))

(defn + [[ax ay] [bx by]]
  [(cljs.core/+ ax bx) (cljs.core/+ ay by)])

(defn -
  ([[x y]]
   [(cljs.core/- x) (cljs.core/- y)])
  ([[ax ay] [bx by]]
   [(cljs.core/- ax bx) (cljs.core/- ay by)]))

(defn * [[x y] scale]
  [(cljs.core/* x scale) (cljs.core/* y scale)])

(defn dot-product
  "Vector dot product"
 [[x0 y0] [x1 y1]]
  (cljs.core/+ (cljs.core/* x0 x1) (cljs.core/* y0 y1)))

(defn cross-product
  "Vector 'cross product', which is a scalar for 2d vectors.
   It's the area of the parallelogram formed by the two vectors."
  [[x0 y0] [x1 y1]]
  (cljs.core/- (cljs.core/* x0 y1) (cljs.core/* x1 y0)))

(defn magnitude [[x y]]
  (Math.sqrt (cljs.core/+ (cljs.core/* x x) (cljs.core/* y y))))

(defn normalize [v]
  (let [length (magnitude v)]
    (if (> length 0) (* v (/ length)) v)))

(defn perpendicular-normal [[x y]]
  (normalize [(cljs.core/- y) x]))

(defn distance [a b]
  (magnitude (- b a)))

(defn rotate
  "rotate a vector by a given rotation"
  [[x y] [r i]]
  [(cljs.core/+ (cljs.core/* r x) (cljs.core/* i (cljs.core/- y)))
   (cljs.core/+ (cljs.core/* i x) (cljs.core/* r y))])

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
  [v translation rotation]
  (let [[x y] v
        [dx dy] translation
        [r i] rotation]
  [(cljs.core/+ (cljs.core/* r x) (cljs.core/* i (cljs.core/- y)) dx)
   (cljs.core/+ (cljs.core/* i x) (cljs.core/* r y) dy)]))
