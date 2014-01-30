(ns pikarush.main
  (:use-macros
   [dommy.macros :only [deftemplate node sel sel1]])
 (:require
   [ajax.core :refer [GET]]
   [clojure.string :as str]
   [dommy.attrs :refer [attr set-attr! toggle-class!]]
   [dommy.core :as dommy]
   [goog.date.Date :as Date]
   [goog.date.DateRange :as DateRange]
   [goog.i18n.DateTimeFormat :as DateTimeFormat]
   [goog.i18n.DateTimeParse :as DateTimeParse]))

;; evil dates have zero-indexed months
(def start-date (goog/date.Date. 2014 1 1))
(def end-date   (goog/date.Date. 2014 1 5))
(def rush-dates (goog/date.DateRange. start-date end-date))

(defn vecify
  "Turn a goog iterator into a cljs vector"
  ([iterator] (vecify iterator []))
  ([iterator s]
    (try
      (let [next (.next iterator)]
        (recur iterator (conj s next)))
      (catch js/Object e
        s))))

(def today (atom (goog/date.Date.)))
(def now   (atom (js/Date.)))

(defn tick! []
  (reset! today (goog/date.Date.))
  (reset! now   (js/Date.)))

(defn before-today? [dt]
  (< (.compare goog/date.Date dt @today) 0))

(defn before-now? [dt]
  (< (.compare goog/date.Date dt @now) 0))

(defn happening-now? [start-time end-time]
  (and (<= (.compare goog/date.Date start-time @now) 0)
       (>= (.compare goog/date.Date end-time @now) 0)))

(defn remaining-rush-days []
  (filter #(not (before-today? %))
          (vecify (.iterator rush-dates))))

(def mmdd-formatter (goog.i18n.DateTimeFormat. "MM/dd"))
(def heading-formatter (goog.i18n.DateTimeFormat. "EEEE, MMMM d"))
(def nav-formatter (goog.i18n.DateTimeFormat. "MMM d"))
(def id-formatter (goog.i18n.DateTimeFormat. "MMMd"))
(def time-formatter (goog.i18n.DateTimeFormat. "hh:mma"))
(def dt-parser (goog.i18n.DateTimeParse. "MM/dd hh:mm"))

;; there's a bug in google closure that makes
;; goog.date.Date objects invalid inputs as
;; the third argument for DateTimeParse.parse
(defn parse-dt [d t]
  (let [dt-str (str d " " t)
        date-obj (js/Date.)]
    (.parse dt-parser dt-str date-obj)
    date-obj))

(deftemplate event-list [events-by-date]
  (let [rush-days (remaining-rush-days)]
    [:div
      (if (empty? rush-days)
        [:h3 "Rush is over. Hope you had a blast! "
             "Feel free to stop by pika regardless."]
        [:ul#dates.pure-paginator
         (for [day rush-days]
          [:li
           [:a.pure-button
            {:href (str "#" (.format id-formatter day))}
            (.format nav-formatter day)]])])

      (for [day rush-days]
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
                                           (:time event))
                    event-now? (happening-now? start-dt end-dt)]
                (when-not (before-now? end-dt)
                  [:div {:id (str "#" event-id)
                         :class (str "event"
                                     (when event-now? " now"))}
                   [:a {:href (str "#" event-id)}
                    [:span.time (str (.format time-formatter start-dt)
                                     " - "
                                     (.format time-formatter end-dt))]
                     [:h4
                      (:name event)]]

                     [:div.description
                       [:p (:description event)]
                       [:span.location
                        [:strong "Location: "] (:location event)]]])))]))]))

(defn toggle-event-descr [elem]
  (fn [e]
    (toggle-class! elem "active")))

(defn render-events [events-by-date]
  (dommy/set-html! (sel1 :#event-list)
                   (dommy/html (event-list events-by-date)))

  (doseq [elem (sel ".event")]
    (dommy/listen! elem :click (toggle-event-descr elem))))

;; make it september 3 for testing
(defn time-machine []
  (reset! today (goog/date.Date. 2013 8 3))
  (reset! now (js/Date. 2013 8 3 19 30)))

(def photos
  ["boat" "costume" "desi" "dinner" "fort"
   "fronthouse" "lion" "porch" "roofdeck" "treehouse"])

(def photo-elem (sel1 :#random-photo))

(defn init [events]
  (let [events-by-date (group-by :date events)]
    (add-watch now :re-render
     (fn [k r o n]
         (render-events events-by-date)
         (dommy/set-attr! photo-elem "src"
                          (str "photos/" (rand-nth photos) ".jpg")))))

  ;; update the schedule every thirty minutes
  (js/setInterval tick! (* 1000 60 30))

  ;; here we go
  (tick!))

(GET "events/spring2014.edn" {:handler init :format :edn})
