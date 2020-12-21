(ns zframes.history
  (:require [zframes.utils   :as u]
            [zframes.routing :as zr]
            [re-frame.core   :as rf]))

(def length 20)

(defn history-push [history page]
  (conj (take length history) page))

(def cofx ::history)
(rf/reg-cofx
 cofx
 (fn [coeffects]
   (assoc coeffects :history (get-in coeffects [:db :history]))))

(rf/reg-event-db
 ::add
 (fn [db [_ page]]
   (update db :history history-push page)))

(defn history [handler & [length]]
  (fn [{current-page :current-page :as req}]
    (-> req
        (u/to-dispatch [::add current-page])
        handler)))

(def back ::back)
(rf/reg-event-fx
 back
 (fn [{db :db} _]
   (let [page (-> db :history second)] 
     {:db (update db :history (partial drop 2))
      :dispatch [::zr/redirect page]})))
