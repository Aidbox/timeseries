(ns app.pages.index.model
  (:require [re-frame.core :as rf]
            [zframes.pages :as pages]))

(def index-page ::index-page)

(rf/reg-event-fx
 index-page
 (fn [{db :db} [pid phase params]]
   {:json/fetch [{:uri "/Patient"
                   :req-id pid}
                 {:uri "/$devices"
                  :req-id ::devices}
                 {:uri "/$alerts"
                  :req-id ::alerts}]}))

(rf/reg-sub
 index-page
 :<- [:xhr/response index-page]
 :<- [:xhr/response ::devices]
 (fn [[pts devices] _]
   {:pts (->> pts :data :entry (map :resource))
    :d (->> devices
            :data
            (reduce
             (fn [acc d] (assoc acc (:device_id d) true)) {}))}))

(rf/reg-sub
 ::alerts
 :<- [:xhr/response ::alerts]
 (fn [alerts _]
   (prn alerts)
   {:alerts  (:data alerts)}))
