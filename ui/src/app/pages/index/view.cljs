(ns app.pages.index.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [app.pages.index.model :as model]))

(defn get-age [bd]
  (let [bd-date (js/Date. bd)
        diff (- (.now js/Date.) (.getTime bd-date))
        age (js/Date. diff)
        full-year (.getUTCFullYear age)]
    (js/Math.abs (- full-year 1970))))

(pages/reg-subs-page
 model/index-page
 (fn [{d :d :as  page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      (for [p (:pts page)]
        [:a {:key (:id p)
             :href (str "#/patient/" (:id p))}
         [:div.pX-30.pY-20.peers.ai-c.bdB.bgcH-grey-100.cur-p {:style {:color "#72777a"}}
          [:div.peer.mR-20
           [:img.bdrs-50p.w-3r.h-3r {:src (str "https://i.pravatar.cc/150?u=" (:id p))}]]
          [:div.peer.peer-greed.pL-20.flex
           [:h5.c-grey-900.mB-5
            (str/replace (str (get-in p [:name 0 :given 0]) " " (get-in p [:name 0 :family])) #"\d+" "")]
           [:div
            [:span.mR-20 {:style {:color "#72777a"}}
             [:i.ti-user.mR-5]
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

           (let [i (:identifier p)
                 ss (:value (first (filter #(= "SS" (-> % :type :coding first :code)) i)))
                 mrn (:value (first (filter #(= "MR" (-> % :type :coding first :code)) i)))]
             [:div
              [:span.mR-5 {:style {:font-size "16px"
                                   :font-weight "300"}} "Security Number"]
              [:span.mR-20 ss]
              [:span.mR-5 {:style {:font-size "16px"
                                   :font-weight "300"}} "MRN"]
              [:span.mR-20 mrn]])]
          (when (get d (:id p))
              [:div.peer

            [:iframe
             {:src (str "http://localhost:3000/d-solo/dLE5W0xMk/devices?orgId=1&from=now-5m&to=now&var-patient_id=" (:id p) "&refresh=5s&panelId=4")
              :height "100px"
              :width "400px"
              :frameBorder   "0"}]])
          ]])]]]))
