(ns app.core
  (:require [aidbox.sdk.core :as sdk]
            [app.db :as db]
            [honeysql.format :as hsformat]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(defonce conn (atom nil))
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

(defn to-date [s]
  (-> s
      java.time.Instant/parse
      java.util.Date/from))


(defn to-ts [s]
  (.getTime ( to-date s)))

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

(defn ts->component [ts-record]
  (let [{:keys [display system code valuequantity_unit valuequantity_value]} ts-record]
    {:code
     {:text display
      :coding
      [{:code    code
        :system  system
        :display display}]}
     :value {:Quantity {:unit valuequantity_unit :value valuequantity_value}}}))

(defn ts-2-observation [observation-id]
  (let [fhir-obs (jdbc/query @conn (hsformat/format
                                    {:select [:*]
                                     :from   [:observation]
                                     :where  [:= :id observation-id]}))
        {:keys [id resource resource_type]} fhir-obs
        ts-obs (jdbc/query @conn (hsformat/format
                                  {:select [:*]
                                   :from [:observation_data]
                                   :where [:= :Observation_id observation-id]}))
        resource* (assoc resource :resourceType resource_type :id id)]
    (assoc resource* :component (mapv ts->component ts-obs))))

;; (jdbc/query
;;    @conn
;;    (hsformat/format
;;     {:select [:*]
;;      :from [:observation_data]
;;      ;; :where [:= :Observation_id "some_id"]
;;      :limit 1
;;      }))

(defn insert-ts-obs [obs]
  (jdbc/query
   @conn
   (hsformat/format
    {:insert-into :observation_data
     :values obs
     :returning [:*]})))

(defmethod sdk/endpoint
  :create-ts-observation
  [ctx {res :resource :as request}]

  {:status 200
   :body (-> res observation-2-ts insert-ts-obs)})


(defonce app-state (atom {}))

(defn mk-connection [state]
  (when @state (reset! state nil))
  (reset! state
          (db/datasource {:host (or (System/getenv "PGHOST") "localhost")
                          :port (or (System/getenv "PGPORT") "5488")
                          :user (or (System/getenv "PGUSER") "postgres")
                          :password (or (System/getenv "PGPASSWORD") "postgres")
                          :database (or (System/getenv "PGDATABASE") "devbox")})))

(defn -main []
  (mk-connection conn)
  (sdk/start* app-state ctx))

(comment
  (mk-connection conn)

  (jdbc/query @conn ["select count(*) from attribute"])


  (jdbc/query
   @conn
   (hsformat/format
    {:insert-into :observation_data
     :values [{:ts "2016-06-06T01:39:47.000Z"}]
     :returning [:*]}))
  )


(comment
  (str (java.sql.Timestamp. (.getTime ( to-date "2016-06-06T01:39:47.01230Z")))))
