(ns sketch.test.helpers
  (:require [sketch.vector :as v])
  (:use-macros
    [purnam.test :only [init]]))

(init) ; initialize purnam js helpers

(def ^:dynamic *near-delta* 0.001)

(defn near-matcher [expected expr]
  (this-as this
           (let [actual (.-actual this)
                 diff (v/distance expected actual)]
             (aset this "message"
                   (fn []
                     (str
                       "Expression: " expr
                       "\nExpected " expected
                          " to be near " actual
                          "\nbut was " diff " away")))
             (< diff *near-delta*))))

(js/beforeEach
  (fn [] (this-as this
                  (.addMatchers this
                                (js-obj "toBeNear" near-matcher)))))
