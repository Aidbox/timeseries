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
   (sut/obs->fhir
    [{:observation_id      "some_id"
     :ts                  "2016-06-06T01:39:47.000Z"
     :effectiveDateTime   "2016-06-06T01:39:47.000Z"
     :code                "8480-6"
     :system              "http://loinc.org"
     :display             "Systolic blood pressure"
     :patient_id          "patient-id"
     :valuequantity_value 130
     :valuequantity_unit  "mmHg"}
    {:observation_id      "some_id"
     :ts                  "2016-06-06T01:39:47.000Z"
     :effectiveDateTime   "2016-06-06T01:39:47.000Z"
     :code                "8462-4"
     :system              "http://loinc.org"
     :display             "Diastolic blood pressure"
     :patient_id          "patient-id"
     :valuequantity_value 70
     :valuequantity_unit  "mmHg"}])

   {:id        "some_id"
    :subject   {:id "patient-id" :resourceType "Patient"}
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

  ;; Match?
  (sut/observation-2-ts
   {:subject {:id "some-ecg-patient"},
    :id "c4670aa9-ee97-4cc8-8a23-e8c55615189b",
    :effective {:instant "2020-06-06T01:39:47.000Z"},
    :component
    [{:code
      {:coding
       [{:code "131329"
         :system "urn:oid:2.16.840.1.113883.6.24"
         :display "MDC_ECG_ELEC_POTL_I"}]}
      :value
      {:SampledData
       {:period 2
        :data
        "-0.115 -0.116 -0.117 -0.118 -0.115 -0.115 -0.115 -0.117 -0.109 -0.108 -0.13 -0.127 -0.121 -0.12 -0.12 -0.12 -0.112 -0.109 -0.111"}}}]})
  [{:valueSampledData_data -0.115}
   {:valueSampledData_data -0.116}
   {:valueSampledData_data -0.117}
   {:valueSampledData_data -0.118}
   {:valueSampledData_data -0.115}
   {:valueSampledData_data -0.115}
   {:valueSampledData_data -0.115}
   {:valueSampledData_data -0.117}
   {:valueSampledData_data -0.109}]

  )
