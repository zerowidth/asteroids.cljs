(ns sketch.test.physics
  (:require [purnam.cljs])
  (:use-macros [purnam.test :only [init describe it is is-not]]))

(init) ; initialize jasmine

(deftest test-physical-properties
  )

(describe
  {:doc "truth"
   :globals [value true]}
  (it "is true"
      (is value true)))
