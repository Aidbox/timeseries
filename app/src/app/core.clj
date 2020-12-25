(ns app.core
  (:require [aidbox.sdk.core :as sdk]
            [aidbox.sdk.crud :as crud]
            [app.db :as db]
            [app.date :as date]
            [clojure.java.io :as io]
            [clojure.core.matrix :as matrix]
            [clojure.data.csv :as csv]
            [honeysql.format :as hsformat]
            [clojure.string :as str]
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

(defn add-ms [s ms]
  (-> s
    to-ts
    (+ ms)
    (java.util.Date. )))



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

(defn String->Number [str]
  (let [n (Float/parseFloat str)]
       (if (number? n) n nil)))

(defn observation-2-ts [resource]
  (let [id (or (:id resource) (gen-guid))
        ts (or (get-in resource [:effective :dateTime])
               (get-in resource [:effective :instant])
               (get-in resource [:effective :Period :start]))
        pt_id (get-in resource [:subject :id])
        dates (mk-obs-date resource)
        component (or (:component resource) [resource])]
    (->> component
         (mapcat
          (fn [c]
            (if (get-in c [:value :SampledData])
              ;; Parse SampledData
              (let [period (get-in c [:value :SampledData :period])
                    code (mk-component-code c)
                    data (-> c
                          (get-in [:value :SampledData :data])
                          (str/split #" "))]
                (map-indexed
                 (fn [idx d]
                   (merge
                    {:Observation_id id
                     :Patient_id pt_id
                     :ts (add-ms ts (* period idx))}
                    code
                    {:valueSampledData_data (String->Number d)}))
                 data))
              ;; Parse regular value
              [(merge
                {:Observation_id id
                 :ts ts
                 :Patient_id pt_id}
                (mk-component-code c)
                dates
                (parse-value (:value c)))]))))))

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

(partition 3 3 nil [1 2 3 4 5 6 7 8 9 0])
(defn insert-ts-obs [obs]
  (->> obs
       (partition 1000 1000 nil)
       (mapcat
        (fn [part]
          (jdbc/query
           @conn
           (hsformat/format
            {:insert-into :observation_data
             :values part
             :returning [:*]}))))))

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

(defn data-file [id]
  (str "/Users/aitem/Work/hackaton/pydata/ptb-xl-1.0.1.physionet.org/csv/"  id ".csv"))


(defn ecg-data [reader]
  (->> reader
       csv/read-csv
       matrix/columns
       (reduce
        (fn [acc c]
          (assoc acc (keyword (first c)) (str/join " " (rest c))))
        {})))

(def req-ctx
  {:app {}
   :client {:id     "root"
            :secret "secret"}
   :box {:base-url  "http://localhost:8888"}})




(comment
  (doseq [n (range 100)]
    (prn "Load " n)

    (time (count (with-open [reader (io/reader (data-file (or (+ 100 n) 1)))]
              (let [data (ecg-data  reader)]
                (-> {:subject {:id (gen-guid)}
                     :id (gen-guid)
                     :effective {:instant (str (date/rand-date "2020-06-01" "2020-12-01")
                                               "T15:00:00.000Z")}

                     :component
                     [{:code {:coding [{:code "131329"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "I"}]}
                       :value {:SampledData {:period 2 :data (:I  data)}}}
                      {:code {:coding [{:code "131330"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "II"}]}
                       :value {:SampledData {:period 2 :data (:II  data)}}}
                      {:code {:coding [{:code "131331"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "III"}]}
                       :value {:SampledData {:period 2 :data (:III  data)}}}
                      {:code {:coding [{:code "131332"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "AVR"}]}
                       :value {:SampledData {:period 2 :data (:AVR  data)}}}
                      {:code {:coding [{:code "131333"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "AVL"}]}
                       :value {:SampledData {:period 2 :data (:AVL  data)}}}
                      {:code {:coding [{:code "131334"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "AVF"}]}
                       :value {:SampledData {:period 2 :data (:AVF  data)}}}
                      ;; V1-V6
                      {:code {:coding [{:code "131341"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V1"}]}
                       :value {:SampledData {:period 2 :data (:V1  data)}}}
                      {:code {:coding [{:code "131342"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V2"}]}
                       :value {:SampledData {:period 2 :data (:V2  data)}}}
                      {:code {:coding [{:code "131343"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V3"}]}
                       :value {:SampledData {:period 2 :data (:V3  data)}}}
                      {:code {:coding [{:code "131344"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V4"}]}
                       :value {:SampledData {:period 2 :data (:V4  data)}}}
                      {:code {:coding [{:code "131345"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V5"}]}
                       :value {:SampledData {:period 2 :data (:V5  data)}}}
                      {:code {:coding [{:code "131346"
                                        :system "urn:oid:2.16.840.1.113883.6.24"
                                        :display "V6"}]}
                       :value {:SampledData {:period 2 :data (:V6  data)}}}
                      ]}

                    observation-2-ts
                    insert-ts-obs
                    )))))))


(comment
 ;; [V1 V2 V3 V4 V5 V6]
  (def file-path "/Users/aitem/Work/hackaton/pydata/ptb-xl-1.0.1.physionet.org/csv/1.csv")


  (with-open [reader (io/reader file-path)]
    (let [[header & content] (csv/read-csv reader)]
      (println header)))
  )
