(ns sketch.test.helpers
  (:require [purnam.test]
            [sketch.vector :as v]))

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
