(ns pikarush.main
  (:use-macros
   [dommy.macros :only [sel sel1]])
 (:require
   [dommy.utils :as utils]
   [dommy.core :as dommy]))

(def now (atom (js/Date.)))
