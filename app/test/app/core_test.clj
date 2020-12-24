(ns app.core-test
  (:require [app.core :as sut]
            [matcho.core :refer [match]]
            [clojure.test :refer :all]))

(deftest parser
  (match
   (sut/observation-2-ts
    {:id   "some_id"
     :subject      {:id "patient-id" :resourceType "Patient"}
     :effective    {:dateTime "2016-06-06T01:39:47.000Z"}
     :component
     [{:code {:coding [{:code    "8480-6"
                        :system  "http://loinc.org"
                        :display "Systolic blood pressure"}]}
       :value {:Quantity {:unit "mmHg" :value 130}}}

      {:code {:coding
              [{:code    "8462-4"
                :system  "http://loinc.org"
                :display "Diastolic blood pressure"}]}
       :value {:Quantity {:unit "mmHg" :value 70}}}]})

   [{:Observation_id "some_id"
     :ts "2016-06-06T01:39:47.000Z"
     :effectiveDateTime "2016-06-06T01:39:47.000Z"
     :code    "8480-6"
     :system  "http://loinc.org"
     :display "Systolic blood pressure"
     :Patient_id  "patient-id"
     :valueQuantity_value 130
     :valueQuantity_unit  "mmHg"}
    {:Observation_id "some_id"
     :ts "2016-06-06T01:39:47.000Z"
     :effectiveDateTime "2016-06-06T01:39:47.000Z"
     :code    "8462-4"
     :system  "http://loinc.org"
     :display "Diastolic blood pressure"
     :Patient_id  "patient-id"
     :valueQuantity_value 70
     :valueQuantity_unit  "mmHg"}]))
