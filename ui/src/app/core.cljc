(ns app.core
  (:require [reagent.dom :as rdom]
            [reagent.core :as r]
            [app.pages.core :as pages]
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
  (rdom/render [root] (.getElementById js/document "root")))
