(ns sketch.test.vector
  (:require [purnam.cljs]
            [sketch.test.helpers]
            [sketch.rotation :as r]
            [sketch.vector :as v])
  (:use-macros
    [sketch.test :only [is-near]]
    [purnam.test :only [describe it is is-not]]))

(def zero (r/from-angle 0))
(def pi (r/from-angle Math/PI))

(describe
  {:doc "rotate"}
  (it "returns the same vector when rotating by 0"
      (is (v/rotate [1 0] zero) [1 0]))
  (it "reverses the vector when rotated by PI"
      (is-near (v/rotate [1 0] pi) [-1 0]))
  (it "rotates by an arbitrary angle"
      (is-near (v/rotate [1 0] (r/from-angle (/ Math/PI 3)))
               [0.5 0.866])))
