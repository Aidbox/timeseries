(ns app.pages.patient.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [reagent.core :as r]
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


(pages/reg-subs-page
 model/view-page
 (fn [{p :pt ecg :ecg :as page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white [:div.pX-30.pY-20.peers.ai-c [:div.peer.mR-20 [:img.bdrs-50p.w-3r.h-3r {:src (str "https://i.pravatar.cc/150?u=" (:id p))}]] (pr-str (:name p))]]]

    [:div.col-md-12.mt-3
     [:div.bd.bgc-white
      [:div.pX-30.pY-20.peers.ai-c
       [:div.layer.w-100
        [:h5.lh-1  "ECG list"]]]
      (for [e ecg] ^{:key (:observation_id e)}
        [ecg-view e p])]]]))
