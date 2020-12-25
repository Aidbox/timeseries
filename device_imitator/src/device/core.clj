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
   :jobs {:j1 {:patient-id "333"
               :dataset-path  "resources/csv/bidmc_01_Numerics.csv"}
          :j2 {:patient-id "123"
               :dataset-path  "resources/csv/bidmc_02_Numerics.csv"}}})


(def patients
  ["d681d725-8353-4996-ae13-a9450efe83d8"
 "d7c00182-eb42-40db-a1a8-32e34d50c79c"
 "d88b850b-f613-4e33-ad19-cbeadc80ba70"
 "d9b90399-0504-4f1e-bf52-2f45354994dd"
 "e5c09aa2-9679-4e25-99b9-cc4dc2493db7"
 "e9adac47-eb98-4fce-b871-512226086c97"
 "ea777ccd-5156-461f-a09f-67bbcaae13dc"
 "ec0115d1-ac56-477e-b27c-0f5c51c0c216"
 "ecd7dffa-53ef-4590-948d-2594265d767b"
 "ed3cec2f-463b-4387-810c-2d519530447a"
 "ef7867d5-2aa4-4963-a041-f6c4184dd956"
 "efb65684-9cef-4e1a-86dd-920fd907696d"
 "efe70546-195b-40b4-9437-9aed9eb55862"
 "f0441407-3b1b-40a9-8b44-cd5ce133988d"
 "f3071490-2b4a-48d3-b3c1-71389efdfb76"
 "f4c654be-9a46-467e-92fc-fde5bb291a1f"
 "f6315469-d594-422a-ba9f-e6cc7548aec1"
 "f715403a-d2d0-4c8f-8fd1-9ee4747fc161"
 "f73ee0fa-a22a-4cba-86cc-11909e2c914e"
 "f83ce939-05b8-4428-805b-f2f118de9416"
 "f9595e93-f819-4714-9756-973709b05f9d"
 "fb675852-247b-4dd4-a0ac-45865d0a389a"
 "fc47806b-3b22-4003-9ff6-5e9c927a8677"
 "fd22f7f8-70a6-4d45-b818-8be4eb2ed0ea"])

(defn configure [n]
  {:box-url "http://localhost:8888"
   :jobs (reduce
          (fn [acc v]
            (assoc acc v
                   {:patient-id (get patients v)
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

  (run-jobs (configure 10))

  (for [x {:a [1 2] :b [2 3]}] x)
  (prn 1)

  (build-observation {:patient-id   "333"
                      :dataset      {:rel-time "111", :hr "92", :pulse "NaN", :resp "23", :spo2 "NaN"}
                      :initial-time (now)})

  (jobs/stop-and-reset-pool! my-pool)

  )
