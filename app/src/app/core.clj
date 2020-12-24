(ns app.core
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
   :manifest
   {:id "timeseries"
    :type "app"
    :operations
    {:create-ts-observation
     {:method "POST" :path ["Observation"]}}}})


(defn gen-guid []
  (str (java.util.UUID/randomUUID)))

(defn mk-component-code [component]
  (-> component
      (get-in [:code :coding 0])
      (select-keys [:code :system :display])))

(defn mk-obs-date [obs]
  {:effectiveDateTime     (get-in obs [:effective :dateTime])
   :effectiveInstant      (get-in obs [:effective :Instant])
   :effectivePeriod_start (get-in obs [:effective :Period :start])
   :effectivePeriod_end   (get-in obs [:effective :Period :end])})

(defn parse-value [value]
  (cond
    (:Quantity value)
    (reduce-kv
     (fn [acc k v]
       (assoc acc (keyword (str "valueQuantity_" (name k))) v))
     {}
     (:Quantity value))

    (:string value)
    {:valueString (:string value)}

    (:boolean value)
    {:valueBoolean (:boolean value)}

    (:integer value)
    {:valueInteger (:integer value)}

    (:time value)
    {:valueTime (:time value)}

    (:DateTime value)
    {:valueDateTime (:DateTime value)}
    ))

(defn observation-2-ts [resource]
  (let [id (or (:id resource) (gen-guid))
        ts (or (get-in resource [:effective :dateTime])
               (get-in resource [:effective :instant])
               (get-in resource [:effective :Period :start]))
        pt_id (get-in resource [:subject :id])
        dates (mk-obs-date resource)
        component (or (:component resource) [resource])]
    (->> component
         (map (fn [c]
                (merge
                 {:Observation_id id
                  :ts ts
                  :Patient_id pt_id}
                 (mk-component-code c)
                 dates
                 (parse-value (:value c))))))))


(defmethod sdk/endpoint
  :create-ts-observation
  [ctx {res :resource :as request}]
  {:status 200
   :body res})


(defonce app-state (atom {}))

(defn -main []
  (sdk/start* app-state ctx))
