(ns app.pages.patient.model
  (:require [re-frame.core :as rf]
            [zframes.pages :as pages]))

(def view-page ::view-page)

(rf/reg-event-fx
 view-page
 (fn [{db :db} [pid phase]]
   {:json/fetch [{:uri (str "/Patient/" (get-in phase [:params :route :id]))
                  :req-id pid}
                 {:uri (str "/$ecg/" (get-in phase [:params :route :id]))
                  :req-id ::ecg}
                 ]}))

(rf/reg-sub
 view-page
 :<- [:xhr/response view-page]
 :<- [:xhr/response ::ecg]
 (fn [[pt ecg] _]
   {:pt (->> pt :data)
    :ecg (:data ecg)}))
