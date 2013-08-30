(defproject pikarush "0.0.1"
  :description "pika rush website 2013"
  :dependencies [[prismatic/dommy "0.1.1"]]
  :plugins [[lein-cljsbuild "0.3.2"]]

  :cljsbuild
    {:builds

     {:dev
       {:source-paths ["src-cljs"]
         :compiler
         {:output-to "script-dev.js"
          :optimizations :whitespace
          :pretty-print true}}

      :release
       {:source-paths ["src-cljs"]
        :compiler
         {:output-to "script.js"
          :optimizations :advanced}}}})