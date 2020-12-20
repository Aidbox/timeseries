(ns zframes.modal
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::modal
 (fn [db [_ modal]]
   (assoc db :modal modal)))

(rf/reg-event-db
 ::close-modal
 (fn [db _]
   (dissoc db :modal)))

(rf/reg-sub
 ::modal
 (fn [db _]
   (:modal db)))

(defn modal []
  (let [modal* (rf/subscribe [::modal])]
    (fn []
      [:div.zmodal "Modal"])))
