(ns app.pages.patient.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [app.pages.patient.model :as model]))

(pages/reg-subs-page
 model/view-page
 (fn [{p :pt ecg :ecg :as page} _]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      [:div.pX-30.pY-20.peers.ai-c
       [:div.peer.mR-20
           [:img.bdrs-50p.w-3r.h-3r {:src (str "https://i.pravatar.cc/150?u=" (:id p))}]]
       (pr-str (:name p))]
      ]



     ]

    [:div.col-md-12.mt-3
     [:div.bd.bgc-white
      [:div.pX-30.pY-20.peers.ai-c
       [:div.layer.w-100.mB-10
        [:h5.lh-1  "ECG list"]]
       (pr-str ecg)]
      ]]
    ]
   ))
