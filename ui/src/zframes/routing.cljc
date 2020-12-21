(ns zframes.routing
  (:require [re-frame.core :as rf]
            [re-frame.db :as db]
            [clojure.string :as str]
            [zframes.utils :as u]
            [route-map.core :as route-map]))

(defn to-query-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (str/join "&")))


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


(defn assoc-routes [db routes]
  (assoc db
         :route-map {:routes routes
                     :routes-idx (routes-idx routes)}))

(rf/reg-event-fx
 ::init
 (fn [{db :db} [_ routes]]
   {:db (assoc-routes db routes)
    ::start {}}))

(defn template-route [route params]
  (map
   (fn [r]
     (if (string? r)
       r
       (get-in params r)))
   route))

(defn ev-href [{:keys [ev route-idx location]
                {:keys [search hash route]} :params}]
  (let [route-idx (or route-idx (get-in @db/app-db [:route :idx]) )
        anchor-route (-> (get route-idx ev)
                         (template-route route)
                         (->> (str/join "/")))
        location     (or location (when search "/") "")
        search-qs    (some->> search to-query-params (str \?))
        anchor-qs    (some->> hash to-query-params (str \?))]
    ;;(prn "to redirect" (str location search-qs anchor-route anchor-qs))
    (str location search-qs anchor-route anchor-qs)))

(defn pushstate [href]
  #?(:cljs (.pushState js/history #js{} (:title "") href)))

(defn redirect [href-struct]
  (let [href (ev-href href-struct)] 
    #?(:cljs (if (empty? (get-in href-struct [:params :search]))
               (set! (.-hash (.-location js/window)) href)
               (pushstate href))
       :clj  nil)))

(defn push-redirect [href-struct]
  (let [href (ev-href href-struct)] 
    #?(:cljs (pushstate href) 
       :clj  nil)))


(rf/reg-fx ::push-redirect push-redirect)

(rf/reg-event-fx
 ::push-redirect
 (fn [_ [_ href-struct]]
   {::push-redirect href-struct}))

(rf/reg-fx ::redirect redirect)

(rf/reg-event-fx
 ::redirect
 (fn [{db :db} [_ href-struct]]
   {::redirect href-struct}))

(rf/reg-event-fx
 ::merge-redirect
 (fn [{db :db} [_ page]]
   (let [current-page (get-in db [:route :current-page])]
     {::redirect (u/deep-merge current-page page)})))
