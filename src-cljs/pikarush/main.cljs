(ns pikarush.main
  (:use-macros
   [dommy.macros :only [sel sel1]])
 (:require
   [dommy.utils :as utils]
   [dommy.core :as dommy]
   [goog.date.Date :as Date]
   [goog.date.DateRange :as DateRange]))

(def start-date (goog/date.Date. 2013 8 31))
(def end-date (goog/date.Date. 2013 9 7))
(def rush-dates (goog/date.DateRange. start-date end-date))

(def now (atom (js/Date.)))
