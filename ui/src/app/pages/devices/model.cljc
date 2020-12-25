(ns app.pages.devices.model
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
                 ]}))

(rf/reg-sub
 index-page
 :<- [:xhr/response index-page]
 :<- [:xhr/response ::devices]
 (fn [[pts devices] _]
   {:pts (->> pts :data :entry (map :resource)
              (reduce
               (fn [acc d] (assoc acc (:id d) d)) {}))
    :d (->> devices :data)}))
