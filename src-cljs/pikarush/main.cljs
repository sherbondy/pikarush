(ns pikarush.main
  (:use-macros
   [dommy.macros :only [deftemplate node sel sel1]])
 (:require
   [ajax.core :refer [GET]]
   [dommy.utils :as utils]
   [dommy.core :as dommy]
   [goog.date.Date :as Date]
   [goog.date.DateRange :as DateRange]
   [goog.i18n.DateTimeFormat :as DateTimeFormat]
   [pikarush.events :refer [events]]))

(def start-date (goog/date.Date. 2013 7 31))
(def end-date   (goog/date.Date. 2013 8 7))
(def rush-dates (goog/date.DateRange. start-date end-date))

(defn vecify
  "Turn a goog iterator into a cljs vector"
  ([iterator] (vecify iterator []))
  ([iterator s]
    (try
      (let [next (.next iterator)]
        (recur iterator (conj s next)))
      (catch js/Error e
        s))))

(def rush-date-vec (vecify (.iterator rush-dates)))
(.log js/console (first rush-date-vec))
(.log js/console (last rush-date-vec))

(def now (atom (goog/date.Date.)))


(def mmdd-formatter (goog.i18n.DateTimeFormat. "MM/dd"))
(def heading-formatter (goog.i18n.DateTimeFormat. "MMMM d"))

(deftemplate event-list [events]
  [:div
    (for [day rush-date-vec]
      [:div
        [:h3 (.format heading-formatter day)]
        (for [event events]
          (when (= (:date event) (.format mmdd-formatter day))
            [:div
             [:h4 (:name event)]]))])])

(defn render-events []
  (dommy/set-html! (sel1 :#event-list)
                   (dommy/html (event-list events))))

;; here we go
(render-events)
