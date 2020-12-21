(ns zframes.pages
  (:require [re-frame.core :as rf]))

(defonce pages (atom {}))
;; TODO: may be defmulti? 

(rf/reg-sub
 ::data
 (fn [db [_ pid]]
   (get db pid)))

(defn get-page [route]
  (get @pages (:match route)))

(defn reg-page [key page]
  (swap! pages assoc key page))

(defn subscribed-page [page-idx view]
  (fn [params]
    (let [m (rf/subscribe [page-idx params])]
      (fn [params] [view @m params]))))

(defn reg-subs-page
  "register subscribed page under keyword for routing"
  [key f]
  (swap! pages assoc key
         (subscribed-page key f)))
