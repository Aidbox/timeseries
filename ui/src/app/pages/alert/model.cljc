(ns app.pages.alert.model
  (:require [re-frame.core :as rf]
            [zframes.pages :as pages]))


(def view-page ::view-page)

(rf/reg-event-fx
 view-page
 (fn [{db :db} [pid phase]]
   (let [type (get-in phase [:params :route :type])]
     {:json/fetch {:uri (str "/$" type)
                   :req-id pid}})))


(rf/reg-sub
 view-page
 :<- [:xhr/response view-page]
 (fn [alerts _]
   {:alerts (group-by :patient_id (:data alerts))}))
