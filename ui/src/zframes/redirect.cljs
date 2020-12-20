(ns zframes.redirect
  (:require [re-frame.core :as rf]
            [zframes.routing]
            [clojure.string :as str]))

(defn template-route [route-hash params]
  (map
   (fn [r]
     (if (string? r)
       r
       (get-in params r)))
   route-hash))

(defn to-query-params [params]
  (->> params
       (map (fn [[k v]] (str (name k) "=" v)))
       (str/join "&")))

(defn ev-href [{:keys [ev location route-idx]
                {:keys [hash search route]} :params}]
  (let [anchor-route (-> (get route-idx ev)
                         (template-route route)
                         (->> (str/join "/")))
        location     (or location (when search "/") "")
        search-qs    (some->> search to-query-params (str \?))
        anchor-qs    (some->> hash   to-query-params (str \?))]
    (str location search-qs anchor-route anchor-qs)))

(defn redirect [href-struct]
  (let [href (ev-href href-struct)] 
    (if (get-in href-struct [:location])
      (set! (.-href (.-location js/window)) href)
      (set! (.-hash (.-location js/window)) href))))
