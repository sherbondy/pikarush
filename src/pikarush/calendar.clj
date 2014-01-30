(ns pikarush.calendar
  (:require [clj-ical.format :as ical]
            [pikarush.events :refer events]
            [clojure.string :as str]))

;; export a ical file from the rush events list

(defn map-to-int [v]
  (map (fn [n] (Integer. n))
       v))

(ical/write-object
 [:vcalendar
  (for [event events]
    [:vevent
     [:summary (:name event)]
     [:description (:description event)]
     ;; ha, hard coding
     (let [year 2014
           [month day] (map-to-int (str/split (:date event) "/"))
           [start-hour start-minute] (map-to-int (str/split (nth time 0) ":"))]
       [:dtstart (org.joda.time.DateTime. (Integer. ))]
       [:dtend   (org.joda.time.DateTime. )])])])
