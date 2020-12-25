(ns app.core
  (:require [reagent.dom :as rdom]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [app.pages.core :as pages]
            #?(:cljs [zframes.xhr :as xhr])
            [app.routes :refer [routes]]
            [app.layout]
            [app.auth]
            [clojure.core.async :as a]
            [app.dispatch :as dispatch]
            [zframes.srv :as srv]))

(def response (r/atom nil))

(defn handler [req]
  (reset! response (dispatch/handler req)))

(def srv  (srv/run handler))

(defn root [] @response)

(defn ^:dev/after-load init []
  (srv)
  (rf/dispatch-sync [::xhr/init {:base-url "http://localhost:8888"}])

  (rdom/render [root] (.getElementById js/document "root")))
