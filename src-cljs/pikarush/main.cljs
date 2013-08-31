(ns pikarush.main
  (:use-macros
   [dommy.macros :only [deftemplate node sel sel1]])
 (:require
   [ajax.core :refer [GET]]
   [clojure.string :as str]
   [dommy.attrs :refer [attr toggle-class!]]
   [dommy.core :as dommy]
   [goog.date.Date :as Date]
   [goog.date.DateRange :as DateRange]
   [goog.i18n.DateTimeFormat :as DateTimeFormat]
   [goog.i18n.DateTimeParse :as DateTimeParse]
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

(def now (atom (goog/date.Date.)))

(def mmdd-formatter (goog.i18n.DateTimeFormat. "MM/dd"))
(def heading-formatter (goog.i18n.DateTimeFormat. "MMMM d"))
(def nav-formatter (goog.i18n.DateTimeFormat. "MMM d"))
(def id-formatter (goog.i18n.DateTimeFormat. "MMMd"))
(def time-formatter (goog.i18n.DateTimeFormat. "h:mma"))
(def dt-parser (goog.i18n.DateTimeParse. "MM/dd H:mm"))

(def events-by-date (group-by :date events))

;; there's a bug in google closure that makes
;; goog.date.Date objects invalid inputs as
;; the third argument for DateTimeParse.parse
(defn parse-dt [d t]
  (let [dt-str (str d " " t)
        date-obj (js/Date.)]
    (.parse dt-parser dt-str date-obj)
    date-obj))

(deftemplate event-list []
  [:div
    [:ul#dates.pure-paginator
     (for [day rush-date-vec]
      [:li [:a.pure-button
            {:href (str "#" (.format id-formatter day))}
            (.format nav-formatter day)]])]
    (for [day rush-date-vec]
      (let [mmdd       (.format mmdd-formatter day)
            day-events (get events-by-date mmdd)
            day-id     (.format id-formatter day)]
        [:div
          [:h3 {:id day-id}
           (.format heading-formatter day)]

          (for [i (range (count day-events))]
            (let [event (nth day-events i)
                  event-id (str day-id "-event" i)
                  [start-dt end-dt] (map (partial parse-dt mmdd)
                                         (:time event))]
              [:div.event {:id (str "#" event-id)}
               [:a {:href (str "#" event-id)}
                 [:h4
                  [:span.time (str (.format time-formatter start-dt)
                                   " - "
                                   (.format time-formatter end-dt))]
                  (:name event)]]

                 [:div.description
                   [:p (:description event)]
                   [:span.location
                    [:strong "Location: "] (:location event)]]]))]))])

(defn toggle-event-descr [elem]
  (fn [e]
    (toggle-class! elem "active")))

(defn render-events []
  (dommy/set-html! (sel1 :#event-list)
                   (dommy/html (event-list)))

  (doseq [elem (sel ".event")]
    (dommy/listen! elem :click (toggle-event-descr elem))))

;; here we go
(render-events)
