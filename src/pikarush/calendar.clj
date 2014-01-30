(ns pikarush.calendar
  (:require [clj-ical.format :as ical]
            [clojure.string :as str]))

;; export a ical file from the rush events list

;; (defn map-to-int [v]
;;   (map (fn [n] (Integer. n))
;;        v))

;; (ical/write-object
;;  [:vcalendar
;;   (for [event events]
;;     [:vevent
;;      [:summary (:name event)]
;;      [:description (:description event)]
;;      ;; ha, hard coding
;;      (let [year 2014
;;            [month day] (map-to-int (str/split (:date event) "/"))
;;            [start-hour start-minute] (map-to-int (str/split (nth time 0) ":"))]
;;        [:dtstart (org.joda.time.DateTime. (Integer. ))]
;;        [:dtend   (org.joda.time.DateTime. )])])])

(def events (read-string (slurp "events/spring2014.edn")))

(defn spit-wiki-text [events]
  (let [events-by-date (group-by :date events)
        dates (sort (keys events-by-date))]
    (apply str
      (for [date dates]
        (str "=== " date " ===\n"
          (apply str
            (for [event (get events-by-date date)]
              (str "==== " (:name event) " ====\n\n"
                   "'''Time:''' " (first (:time event)) "-" (second (:time event)) "\n\n"
                   "'''Location:''' " (:location event) "\n\n"
                   (:description event) "\n\n"))))))))

(spit "events.mw" (spit-wiki-text events))
