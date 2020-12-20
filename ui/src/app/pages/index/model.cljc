(ns app.pages.index.model
  (:require [re-frame.core :as rf]))

(def index-page ::index-page)

(rf/reg-event-fx
 index-page
 (fn [{db :db} [pid phase params]]
   ;; Page life cycle
   {}
   ))

(rf/reg-sub
 index-page
 (fn [db _]
   {:header "Index page"}))
