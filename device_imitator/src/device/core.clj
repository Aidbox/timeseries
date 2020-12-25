(ns device.core
  (:require [org.httpkit.client :as http]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [overtone.at-at :as jobs]))

(defn gen-guid []
  (str (java.util.UUID/randomUUID)))

(def config
  {:box-url "http://localhost:8888"
   :jobs {:j1 {:patient-id "bb1cf28f-b3b6-5a90-22f3-71dcecb6fad5"
               :dataset-path  "resources/csv/bidmc_01_Numerics.csv"}
          :j2 {:patient-id "bf16eed6-d4f0-c2f4-9ab4-788f458de47b"
               :dataset-path  "resources/csv/bidmc_02_Numerics.csv"}
          :j3 {:patient-id "bfd8ecb3-06dd-8f08-7121-020a9a589602"
               :dataset-path  "resources/csv/bidmc_03_Numerics.csv"}
          :j4 {:patient-id "eb277a4d-4a42-7905-5576-d92a9a3fda9c"
               :dataset-path  "resources/csv/bidmc_04_Numerics.csv"}
          :j5 {:patient-id "e490c516-7f1e-7910-44d4-0ba0fe542b31"
               :dataset-path  "resources/csv/bidmc_05_Numerics.csv"}
          :j6 {:patient-id "e2d08a03-e382-c8cf-bc7a-474277238b3b"
               :dataset-path  "resources/csv/bidmc_06_Numerics.csv"}
          :j7 {:patient-id "e1ed357c-6753-fe21-f98d-2e5996966ff2"
               :dataset-path  "resources/csv/bidmc_07_Numerics.csv"}
          :j8 {:patient-id "e490c516-7f1e-7910-44d4-0ba0fe542b31"
               :dataset-path  "resources/csv/bidmc_08_Numerics.csv"}
          :j9 {:patient-id "dd178782-f1bd-7487-e670-32e02d2c8396"
               :dataset-path  "resources/csv/bidmc_09_Numerics.csv"}
          :j10 {:patient-id "bfd8ecb3-06dd-8f08-7121-020a9a589602"
               :dataset-path  "resources/csv/bidmc_10_Numerics.csv"}
          :j11 {:patient-id "bf16eed6-d4f0-c2f4-9ab4-788f458de47b"
               :dataset-path  "resources/csv/bidmc_11_Numerics.csv"}
          :j12 {:patient-id "bb1cf28f-b3b6-5a90-22f3-71dcecb6fad5"
               :dataset-path  "resources/csv/bidmc_12_Numerics.csv"}
          }})

(def ids
  [
"bffc6742-0671-0f7f-3837-d3ecdbb31e8b"
"c4f4d2df-7231-9680-0823-3010a4f1a5fa"
"c735a1c7-de3b-e8dc-5383-effc04383d92"
"c81ef392-ed3f-e948-ac90-2e643346ed2d"
"cdd9feca-447c-3d67-dda7-82c975b5aa1a"
"d208b5b9-cc46-9a45-a5d5-b8a2f011de8f"
"d5743f3a-b161-67db-d952-983f5544f7a7"
"d6da7a48-0b77-234e-9c12-38d8be1e00a1"
"db390c71-323a-5ee1-e62d-fe5cffd5c7b9"
"dd178782-f1bd-7487-e670-32e02d2c8396"
"de193121-01a5-8abe-123f-f914f1cc48bb"
"e1ed357c-6753-fe21-f98d-2e5996966ff2"
"e2d08a03-e382-c8cf-bc7a-474277238b3b"
"e3792593-e73d-a677-9f88-d16b51028811"
"e490c516-7f1e-7910-44d4-0ba0fe542b31"
"e51759b6-8c04-ca89-394e-6faa6a736c6f"
"e51d0cfe-a011-908f-29f7-f882725b4146"
"e978f328-bfb3-f0d6-8d74-54dd550f83b3"
"eb277a4d-4a42-7905-5576-d92a9a3fda9c"
"eb4b5684-60e3-625c-56d7-542e8b0d2c3b"
"ebbd68bb-1160-0a61-9aa7-392fb5d9e1a7"
   ]
  )


(defn configure [n]
  {:box-url "http://localhost:8888"
   :jobs (reduce
          (fn [acc v]
            (assoc acc v
                   {:patient-id ( get ids v)
                    :device-id ( get ids v)
                    :dataset-path (str "resources/csv/bidmc_"  (format "%02d" v) "_Numerics.csv")}))
          {}
          (range 1 (inc n)))})

(defn now []
  (new java.util.Date))

(defn ->int [value]
  (Integer/parseInt value))

(defmulti map-component key)

(defmethod map-component :hr [[_ value]]
  {:code
   {:text "Heart rate"
    :coding
    [{:code    "8867-4"
      :system  "http://loinc.org"
      :display "Heart rate"}]}
   :value {:Quantity {:unit "bpm" :value (->int value)}}})

(defmethod map-component :pulse [[_ value]]
  {:code
   {:text "Pulse"
    :coding
    [{:code    "8867-3"
      :system  "http://loinc.org"
      :display "Pulse"}]}
   :value {:Quantity {:unit "bpm" :value (->int value)}}})


(defmethod map-component :resp [[_ value]]
  {:code
   {:text "Respiratory rate"
    :coding
    [{:code    "9279-1"
      :system  "http://loinc.org"
      :display "Respiratory rate"}]}
   :value {:Quantity {:unit "breaths/minute" :value (->int value)}}})


(defmethod map-component :spo2 [[_ value]]
  {:code
   {:text "Oxygen saturation in Arterial blood"
    :coding
    [{:code    "2708-6"
      :system  "http://loinc.org"
      :display "Oxygen saturation in Arterial blood"}]}
   :value {:Quantity {:unit "%" :value (->int value)}}})


(defn build-observation [{:keys [patient-id dataset initial-time]}]
  (let [{:keys [rel-time]} dataset
        additional-seconds (Integer/parseInt rel-time)
        date-time          (-> initial-time
                               .toInstant
                               (.plusSeconds additional-seconds)
                               .toString)
        params             (remove #(= "NaN" (val %)) (dissoc dataset :rel-time))]
    {:resourceType "Observation"
     :subject      {:id patient-id :resourceType "Patient"}
     :device       {:id patient-id :resourceType "Device"}
     :status       "final"
     :effective    {:dateTime date-time}
     :category
     [{:coding
       [{:code    "vital-signs"
         :system
         "http://terminology.hl7.org/CodeSystem/observation-category"
         :display "Vital Signs"}]}]
     :code
     {:text "Blood pressure systolic & diastolic"
      :coding
      [{:code    "8716-3"
        :system  "http://loinc.org"
        :display "Vital signs"}]}
     :component    (mapv map-component params)}))


(defn req-get [{:keys [uri options]}]
  @(http/get (str (:box-url config) uri)
             (merge {:basic-auth ["root" "secret"]}
                    options)))

(defn req-put [{:keys [uri options]}]
  @(http/post (str (:box-url config) uri)
             (merge {:basic-auth ["root" "secret"]
                     :headers    {"content-type" "application/json"}}
                    (update options :body json/generate-string))))


(defn parse-csv [file-path]
  (let [content (with-open [reader (io/reader file-path)]
                  (doall
                   (csv/read-csv reader)))
        body (rest content)]
    (map (partial zipmap [:rel-time :hr :pulse :resp :spo2]) body)))


(defn send-observation [resource]
  (prn "send")
  (req-put {:uri     "/Observation"
            :options {:body resource}}))

(def schedule (atom {}))
(def my-pool (jobs/mk-pool))

(defn consume [{:keys [patient-id initial-time]}]
  (let [data     (first (get @schedule patient-id))
        resource (build-observation {:patient-id   patient-id
                                     :dataset      data
                                     :initial-time initial-time})]
    (send-observation resource)
    (swap! schedule update patient-id (fn [items]
                                        (let [{:keys [rel-time]} (last items)
                                              rel-time* (str (inc (Integer/parseInt rel-time)))
                                              new-item (assoc data :rel-time rel-time*)]
                                          (concat (rest items) (list new-item)))))))


(defn run-job [{:keys [patient-id dataset-path] :as opts}]
  (let [dataset      (parse-csv dataset-path)
        initial-time (now)
        _            (swap! schedule assoc patient-id dataset)]
    (jobs/every 1000 #(consume {:patient-id   patient-id
                                :initial-time initial-time}) my-pool)))

(defn run-jobs [{:keys [jobs]}]
  (reset! schedule {})
  (doall
   (for [params (vals jobs)]
     (run-job params))))


(comment

  (parse-csv  "resources/csv/bidmc_01_Numerics.csv")

  (run-jobs (configure 15))


  #_(run-jobs config)


  (build-observation {:patient-id   "333"
                      :dataset      {:rel-time "111", :hr "92", :pulse "NaN", :resp "23", :spo2 "NaN"}
                      :initial-time (now)})

  (jobs/stop-and-reset-pool! my-pool)

  )
