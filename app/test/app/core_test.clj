(ns app.core-test
  (:require [app.core :as sut]
            [matcho.core :refer [match]]
            [clojure.test :refer :all]))

(deftest parser
  (match
   (sut/observation-2-ts
    {:id        "some_id"
     :subject   {:id "patient-id" :resourceType "Patient"}
     :effective {:dateTime "2016-06-06T01:39:47.000Z"}
     :component
     [{:code  {:coding [{:code    "8480-6"
                         :system  "http://loinc.org"
                         :display "Systolic blood pressure"}]}
       :value {:Quantity {:unit "mmHg" :value 130}}}

      {:code  {:coding
               [{:code    "8462-4"
                 :system  "http://loinc.org"
                 :display "Diastolic blood pressure"}]}
       :value {:Quantity {:unit "mmHg" :value 70}}}]})

   [{:Observation_id      "some_id"
     :ts                  "2016-06-06T01:39:47.000Z"
     :effectiveDateTime   "2016-06-06T01:39:47.000Z"
     :code                "8480-6"
     :system              "http://loinc.org"
     :display             "Systolic blood pressure"
     :Patient_id          "patient-id"
     :valueQuantity_value 130
     :valueQuantity_unit  "mmHg"}
    {:Observation_id      "some_id"
     :ts                  "2016-06-06T01:39:47.000Z"
     :effectiveDateTime   "2016-06-06T01:39:47.000Z"
     :code                "8462-4"
     :system              "http://loinc.org"
     :display             "Diastolic blood pressure"
     :Patient_id          "patient-id"
     :valueQuantity_value 70
     :valueQuantity_unit  "mmHg"}])

  (match
   (sut/ts-2-observation
    "some_id")

   {:id        "some_id"
    :subject   {:id "patient-id" :resourceType "Patient"}
    :effective {:dateTime "2016-06-06T01:39:47.000Z"}
    :component
    [{:code  {:coding [{:code    "8480-6"
                        :system  "http://loinc.org"
                        :display "Systolic blood pressure"}]}
      :value {:Quantity {:unit "mmHg" :value 130}}}

     {:code  {:coding
              [{:code    "8462-4"
                :system  "http://loinc.org"
                :display "Diastolic blood pressure"}]}
      :value {:Quantity {:unit "mmHg" :value 70}}}]}))


  ;; ;; :patient_id = "cfd9e448-0697-409e-a9e3-1fb7403b199c"
  ;; ;; :system = "http://loinc.org"
  ;; :valuestring = nil
  ;; :valuedatetime = nil
  ;; :valuetime = nil
  ;; :valuequantity_system = nil
  ;; :effectiveperiod_start = nil
  ;; :effectiveperiod_end = nil
  ;; :valueinteger = nil
  ;; :ts = "2020-12-24T19:05:43.088Z"
  ;; :valuequantity_code = nil
  ;; :valuequantity_comparator = nil
  ;; ;; :code = "8867-4"
  ;; :effectiveinstant = nil
  ;; ;; :display = "Heart rate"
  ;; :valueboolean = nil
  ;; ;; :valuequantity_unit = "bpm"
  ;; :effectivedatetime = "2020-12-24T19:05:43.088Z"
  ;; :valueperiod_start = nil
  ;; ;; :observation_id = "5a13666f-a560-475a-bb58-a3ef750511ed"
  ;; ;; :valuequantity_value = 101M
  ;; :valueperiod_end = nil

