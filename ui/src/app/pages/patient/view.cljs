(ns app.pages.patient.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [clojure.string :as str]
            [app.pages.patient.model :as model]))

(defn ecg-view [e p]
  (let [st (r/atom false)]
    (fn [e]
      [:div
       [:div.pX-30.pY-20.peers.ai-c.bdT.bgcH-grey-100.cur-p ;;.jc-sb
        {:on-click #(reset! st (not @st))}
        [:div
         [:div.badge.badge-pill.bgc-blue-50.c-blue-700
          [:i.fa.fa-heartbeat.c-blue-700] "12-lead ECG"]
         "  " (:effectiveinstant e)]]

       (when @st
         (let [from (- (.getTime (js/Date. (:effectiveinstant e))) 500 )
               to   (+ from 11000)]
           [:iframe
            {:src (str "http://localhost:3000/d-solo/rb_qDpxGz/new-dashboard-copy?orgId=1&from=" from "&to=" to "&var-Patient=" (:id p) "&panelId=9")
             :height "500px"
             :width  "100%"
             :frameborder   "0"}]))])))


(defn get-age [bd]
  (let [bd-date (js/Date. bd)
        diff (- (.now js/Date.) (.getTime bd-date))
        age (js/Date. diff)
        full-year (.getUTCFullYear age)]
    (js/Math.abs (- full-year 1970))))


(pages/reg-subs-page
 model/view-page
 (fn [{p :pt ecg :ecg :as page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      [:div.pX-30.pY-20.peers.ai-c
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
           [:span.mR-20 mrn]])]]]]

    [:div.col-md-12.mt-3
     [:div.bd.bgc-white
      [:div.pX-30.pY-20.peers.ai-c
       [:div.layer.w-100
        [:h5.lh-1  "ECG list"]]]
      (for [e ecg] ^{:key (:observation_id e)}
        [ecg-view e p])]]]))
