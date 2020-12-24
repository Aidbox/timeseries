(ns device.core
  (:require [org.httpkit.client :as http]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [overtone.at-at :as jobs]))

(def config
  {:box-url "http://localhost:8888"
   :jobs {:j1 {:patient-id "333"
               :dataset-path  "resources/csv/bidmc_01_Numerics.csv"}
          :j2 {:patient-id "123"
               :dataset-path  "resources/csv/bidmc_02_Numerics.csv"}}})

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
    (swap! schedule update patient-id rest)))
(defn run-job [{:keys [patient-id dataset-path] :as opts}]
  (let [dataset      (parse-csv dataset-path)
        initial-time (now)
        _            (swap! schedule assoc patient-id dataset)]
    (jobs/every 1000 #(consume {:patient-id   patient-id
                                :initial-time initial-time}) my-pool)))

(defn run-jobs [{:keys [jobs]}]
  (doall
   (for [params (vals jobs)]
     (run-job params))))


(comment

  (parse-csv  "resources/csv/bidmc_01_Numerics.csv")

  (run-jobs config)

  (prn 1)

  (build-observation {:patient-id   "333"
                      :dataset      {:rel-time "111", :hr "92", :pulse "NaN", :resp "23", :spo2 "NaN"}
                      :initial-time (now)})

  (jobs/stop-and-reset-pool! my-pool)

  )
