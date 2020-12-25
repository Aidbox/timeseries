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
          ]])]]]))
