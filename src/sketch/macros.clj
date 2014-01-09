(ns sketch.macros)

(defmacro p [& more]
  `(.log js/console ~@more))
(defmacro pp [& more]
  `(.apply (.-log js/console) js/console
           (into-array (map #(if (coll? %) (pr-str %) %) (list ~@more)))))
