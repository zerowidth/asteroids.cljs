(ns sketch.macros)

(defmacro p [& more]
  `(.apply (.-log js/console) js/console (into-array (map pr-str '~more ))))
