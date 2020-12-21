(ns app.layout
  (:require [zframes.layout :as zl]
            [zframes.styles :as s]
            [zframes.routing :as zr]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [app.auth :as auth]))

(def app-styles
  (s/styles
   []))

(defmethod zl/layout :main
  [req]
  (let [user (rf/subscribe [::auth/user])]
    (fn [{page :current-page :as req} resp]
      [:div.root.main-layout.grid app-styles
       [:div.content resp]])))
