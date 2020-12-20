(ns zframes.loader
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 ::start
 (fn [db _]
   (assoc-in db [:global :loading] true)))

(rf/reg-event-db
 ::end
 (fn [db _]
   (assoc-in db [:global :loading] false)))

(rf/reg-sub
 ::loader
 (fn [db _]
   (get-in db [:global :loading])))

(defn spinner [& [opts]]
  (let [loading (rf/subscribe [::loader])]
    (fn []
      [:div.zspinner "Loading'"])))
