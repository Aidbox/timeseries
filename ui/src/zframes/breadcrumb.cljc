(ns zframes.breadcrumb
  (:require [re-frame.core :as rf]
            [re-frame.interop :refer [make-reaction]]))

(defn mk-breadcrumbs [route]
  (->> (or (:parents route) [])
       (reduce (fn [acc v]
                 (let [prev (last acc)
                       uri (or (:href prev) "#")
                       p  (last (for [[k route] prev :when (= (:. route) (:. v))] k))
                       p (or (get-in route [:params (first p)]) p)
                       id  (:. v)]
                   (conj acc (assoc v :href (str uri p  "/")))))
               [])
       (filter :breadcrumb)
       (mapv #(select-keys % [:href :breadcrumb]))
       (reduce (fn [acc v]
                 (conj acc (assoc v :display
                                  (let [breadcrumb (:breadcrumb v)]
                                    (if (string? breadcrumb)
                                      breadcrumb
                                      @(rf/subscribe breadcrumb)))))) [])))

(rf/reg-sub-raw
 ::breadcrumb
 (fn [db _]
   (make-reaction
    (fn [] (mk-breadcrumbs @(rf/subscribe [:route-map/current-route]))))))
