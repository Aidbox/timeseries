(ns app.dispatch
  (:require [app.auth :as auth]
            [app.routes :as routes]
            [re-frame.core :as rf]
            [zframes.pages :as pages]
            [zframes.layout :as zl]
            [zframes.middleware :as mw]))

(rf/reg-event-fx
 ::dispatch
 (fn [_ [_ fx]] fx))

(defn render
  "Dispatch page evt and return page content"
  [{:keys [fx ctx-fx] :as req}]
  (rf/dispatch [::dispatch fx])

  [:<>
   [(zl/layout req) req [(pages/get-page (:route req)) (:current-page req)]]])

(def handler
  (-> render
      (mw/context routes/routes)
      mw/page
      mw/history
      (mw/route-map routes/routes)))
