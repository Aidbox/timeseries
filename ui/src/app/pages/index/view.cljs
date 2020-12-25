(ns app.pages.index.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [app.pages.index.model :as model]))

(pages/reg-subs-page
 model/index-page
 (fn [page _]
   [:div
    [:div.row

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
         :frameborder   "0"}]]]]]
   ))
