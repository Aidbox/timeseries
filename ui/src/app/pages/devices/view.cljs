(ns app.pages.devices.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [reagent.core :as r]
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


(defn device-view [did pts]
  (let [s (r/atom false)]
    (fn [did pts]
      [:div.pX-30.pY-20.bdB.bgcH-grey-100.cur-p
       {:on-click #(reset! s (not @s))}
       [:div.peers
        [:div.peer.mR-20
         [:img.bdrs-50p.w-3r.h-3r {:src (:ico (device-ico did))}]]

        (let [p (get pts did)]
          [:div.peer.peer-greed.pL-20.flex
           [:h5.c-grey-900.mB-5
            (:name (device-ico did))]
           [:div
            [:span.mR-20 {:style {:color "#72777a"}}
             [:a {:href (str "#/patient/" (:id p))}
              (str/replace (str (get-in p [:name 0 :given 0]) " " (get-in p [:name 0 :family])) #"\d+" "")]
             [:i.ti-user.mR-5.mL-5]
             (str (first (str/capitalize (or (:gender p) ""))) " " (get-age (:birthDate p)) "y/o")]

            (when-let [a (:address p)]
              (let [{:keys [city line state postalCode]} (first a)]
                [:span.mR-20 {:style {:color "#72777a"}}
                 [:i.ti-home.mR-5] (str (first line) " " city " " state "  " postalCode)]))

            (when-let [t (:telecom p)]
              [:span.mR-20 {:style {:color "#72777a"}}
               [:i.ti-mobile.mR-5] (:value (first t))])]])]

       (when @s
         [:div.row
          [:div.col-md-8.mt-3
           [:iframe.iframe
            {:src (str "http://localhost:3000/d-solo/9AXnT0bGz/device-view?orgId=1&from=now-10m&to=now&var-patient=" did "&refresh=5s&panelId=5")
             :height "200px" :width "100%" :frameBorder   "0"}]]
          [:div.col-md-4.mt-3
           [:iframe
            {:src (str "http://localhost:3000/d-solo/9AXnT0bGz/device-view?orgId=1&from=now-10m&to=now&var-patient=" did "&refresh=5s&panelId=2")
             :height "100px" :width "100%" :frameBorder   "0"}]
           [:iframe
            {:src (str "http://localhost:3000/d-solo/9AXnT0bGz/device-view?orgId=1&from=now-10m&to=now&var-patient=" did "&refresh=5s&panelId=4")
             :height "100px" :width "100%" :frameBorder   "0"}]
           ]
          ]
         )

       ])))

(pages/reg-subs-page
 model/index-page
 (fn [{dv :d pts :pts :as  page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      (for [{did :device_id} dv]
        ^{:key did}
        [device-view did pts])]]]))
