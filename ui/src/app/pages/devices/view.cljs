(ns app.pages.devices.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [app.pages.devices.model :as model]))

(defn get-age [bd]
  (let [bd-date (js/Date. bd)
        diff (- (.now js/Date.) (.getTime bd-date))
        age (js/Date. diff)
        full-year (.getUTCFullYear age)]
    (js/Math.abs (- full-year 1970))))

(def img [{:ico "/img/apple.jpg"
           :name "Apple Watch"}
          {:ico "/img/mi.webp"
           :name "Mi Band"}
          {:ico "/img/sam.png"
           :name "Samsung Gear"}])

(defn device-ico [s]
  (get  img (mod (hash s) 3)))


(pages/reg-subs-page
 model/index-page
 (fn [{dv :d pts :pts :as  page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      (for [{did :device_id} dv]
        ^{:key did}
        [:div.pX-30.pY-20.peers.ai-c.bdB.bgcH-grey-100.cur-p
         [:div.peer.mR-20
          [:img.bdrs-50p.w-3r.h-3r {:src (:ico (device-ico did))}]]

         (let [p (get pts did)]
           [:div.peer.peer-greed.pL-20.flex
            [:h5.c-grey-900.mB-5
             (:name (device-ico did))]
            [:div
             [:span.mR-20 {:style {:color "#72777a"}}
              (str/replace (str (get-in p [:name 0 :given 0]) " " (get-in p [:name 0 :family])) #"\d+" "")
              [:i.ti-user.mR-5.mL-5]
              (str
               (first (str/capitalize (or (:gender p) ""))) " "
               (get-age (:birthDate p)) "y/o")]

             (when-let [a (:address p)]
               (let [{:keys [city line state postalCode]} (first a)]
                 [:span.mR-20 {:style {:color "#72777a"}}
                  [:i.ti-home.mR-5]
                  (str (first line) " " city " " state "  " postalCode)]))

             (when-let [t (:telecom p)]
               [:span.mR-20 {:style {:color "#72777a"}}
                [:i.ti-mobile.mR-5]
                (:value (first t))])]

            ])

         ])]]]

   ))
