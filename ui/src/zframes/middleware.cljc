(ns zframes.middleware
  (:require [re-frame.core :as rf]
            [zframes.layout :as zl]
            [zframes.utils :as u]
            [zframes.history :as zh]
            [clojure.core.async :as async]
            [zframes.context :as zc]
            [route-map.core :as route-map]))

(defn routes-idx
  ([routes path]
   (merge
    {(:. routes) path}
    (reduce-kv
     (fn [acc pth route]
       (if-let [event (:. route)]
         (let [path (conj path pth)]
           (merge
            (assoc acc event path)
            (routes-idx route path)))
         acc))
     {}
     routes)))
  ([routes] (routes-idx routes ["#"])))

(rf/reg-event-db
 ::route-map
 (fn [db [_ idx]]
   (assoc-in db [:route :idx] idx)))

(rf/reg-event-db
 ::current-page
 (fn [db [_ page]]
   (assoc-in db [:route :current-page] page)))

(defn route-map [handler routes]
  (let [idx (routes-idx routes)]
    (rf/dispatch [::route-map idx])
    (fn [req]
      (let [match (route-map/match [:. (:hash-path req)] routes)
            route (merge (-> match :parents last) match)
            current-page {:ev (:match route)
                          :params {:route  (:params route)
                                   :hash   (:hash-params req)
                                   :search (:search-params req)}}
            req (-> req
                    (assoc :route        route
                           :route-idx    idx
                           :current-page current-page)
                    (u/to-dispatch [::current-page current-page]))]
        (handler req)))))


(defn page [handler]
  (fn [{current-page :current-page :as req}]
    (-> req
        (u/to-dispatch [(:ev current-page) current-page])
        handler)))

(def context zc/context)
(def history zh/history)
