(ns aidbox.timeseries
  (:require [aidbox.sdk.core :as sdk])
  (:gen-class))

(def env
  {:init-url           (or (System/getenv "APP_INIT_URL")           "http://localhost:8888")
   :init-client-id     (or (System/getenv "APP_INIT_CLIENT_ID")     "root")
   :init-client-secret (or (System/getenv "APP_INIT_CLIENT_SECRET") "secret")

   :app-id             (or (System/getenv "APP_ID")                 "timeseries")
   :app-url            (or (System/getenv "APP_URL")                "http://host.docker.internal:8989")
   :app-port           (Integer/parseInt
                        (or (System/getenv "APP_PORT")              "8989"))
   :app-secret         (or  (System/getenv "APP_SECRET")            "secret")})


(def ctx
  {:env env
   :manifest {:id "timeseries"
              :type "app"
              :operations
              {:hello {:method "GET"
                       :path ["hello"]}}}})


(defmethod sdk/endpoint
  :hello
  [ctx {id :id}]
  {:status 200
   :body {:hello "world"}})


(defonce app-state (atom {}))

(defn -main []
  (sdk/start* app-state ctx))
