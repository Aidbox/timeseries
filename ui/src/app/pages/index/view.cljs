(ns app.pages.index.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [app.pages.index.model :as model]))

(pages/reg-subs-page
 model/index-page
 (fn [page _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      (for [p (:pts page)]
        [:a {:key (:id p)
             :href (str "#/patient/" (:id p))}
         [:div.pX-30.pY-20.peers.ai-c.bdB.bgcH-grey-100.cur-p ;;.jc-sb
          [:div.peer.mR-20
           [:img.bdrs-50p.w-3r.h-3r {:src (str "https://i.pravatar.cc/150?u=" (:id p))}]]
          [:div.peer
           [:small {:style {:color "#72777a"}} (:birthDate p)]
           [:h5.c-grey-900.mB-5
            (get-in p [:name 0 :given 0])
            " "
            (get-in p [:name 0 :family])
            ]]
          ]]


        )]]



    [:div.col-md-12
     [:iframe
      {:src "http://localhost:3000/d-solo/rb_qDpxGz/new-dashboard-copy?orgId=1&from=1591109999882&to=1591110010126&var-Patient=33b9e41c-6a40-4b8c-948f-e69f5a35da8d&panelId=9"
       :height "500px"
       :width  "100%"
       :frameborder   "0"}]]

    [:div.col-md-6
     [:div.bd

      [:iframe
       {:src "http://localhost:3000/d-solo/rb_qDpxGz/new-dashboard-copy?orgId=1&from=1608823166525&to=1608823640459&var-Patient=43284e5d-c335-431e-a5df-d5b93badacc3&panelId=2"
        :height "300px"
        :width  "100%"
        :frameborder   "0"}]]]]
   ))
