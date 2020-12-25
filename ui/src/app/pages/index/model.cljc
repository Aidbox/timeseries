(ns app.pages.index.model
  (:require [re-frame.core :as rf]
            [zframes.pages :as pages]))

(def index-page ::index-page)

(rf/reg-event-fx
 index-page
 (fn [{db :db} [pid phase params]]
   {:json/fetch {:uri "/Patient"
                 :req-id pid}}))

(rf/reg-sub
 index-page
 :<- [:xhr/response index-page]
 :<- [:xhr/response ::alert-count]
 (fn [[pts alerts] _]
   {:pts (->> pts :data :entry (map :resource))
    :counts (->> alerts :data (map :patient_id) distinct count)}))


(rf/reg-event-fx
 index-page
 (fn [{db :db} [pid phase params]]
   {:json/fetch {:uri "/Patient"
                 :req-id pid}}))


(rf/reg-event-fx
 ::alert-count
 (fn [{db :db} [pid phase]]
   (let [type (get-in phase [:params :route :type])]
     {:json/fetch {:uri (str "/$" type)
                   :req-id pid}})))
