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
 (fn [pts _]
   {:pts (->> pts :data :entry (map :resource))}))
