(ns pikarush.calendar
  (:require [clj-ical.format :as ical]
            [clojure.string :as str])
  (:import org.joda.time.DateTime))

;; export a ical file from the rush events list

(defn map-to-int [v]
  (map (fn [n] (Integer. n))
       v))

(def events (read-string (slurp "events/spring2014.edn")))

(defn ical-event [event]
  (with-out-str
    (ical/write-object
     (let [year 2014
           [month day]               (map-to-int (str/split (:date event) #"/"))
           [start-hour start-minute] (map-to-int (str/split (nth (:time event) 0) #":"))
           [end-hour end-minute]     (map-to-int (str/split (nth (:time event) 1) #":"))]
       [:vcalendar
         [:vevent
           [:summary (:name event)]
           [:description (:description event)]
           ;; ha, hard coding
           [:dtstart (org.joda.time.DateTime. 2014 month day start-hour start-minute)]
           [:dtend   (org.joda.time.DateTime. 2014 month day end-hour end-minute)]]]))))


(defn make-calendars [events]
  (doseq [i (range (count events))]
    (spit (str "events/cals/" i ".ics")
          (ical-event (nth events i)))))

(defn wiki-text [events]
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


(defn -main []
  (make-calendars events)
  (spit "events.mw" (wiki-text events)))
