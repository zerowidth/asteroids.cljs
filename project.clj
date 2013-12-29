(defproject sketch "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2127"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "sketch"
              :source-paths ["src"]
              :compiler {
                :output-to "public/sketch.js"
                :output-dir "public/js"
                :optimizations :none
                :source-map true}}]})
