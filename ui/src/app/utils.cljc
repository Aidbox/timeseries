(ns app.utils
  (:require [chrono.core :as ch]
            [chrono.now :as now]
            [app.translations.core :refer [current-locale]]
            [clojure.string :as str]))

(defn today? [parsed-date]
  (= (ch/format parsed-date [:year :month :day])
     (ch/format (now/local) [:year :month :day])))


(defn format-us-time [parsed-date]
  (let [locale (or (current-locale) :en)
        pm? (<= 12 (:hour parsed-date) 23)]
    (if pm?
      (ch/format (update parsed-date :hour (fn [hour]
                                             (case hour
                                               12 12
                                               (- hour 12))))  (with-meta [:hour ":" :min \space "PM"] {locale true}))
      (ch/format (update parsed-date :hour (fn [hour]
                                             (case hour
                                               0 12
                                               24 12
                                               hour))) (with-meta [:hour ":" :min \space "AM"] {locale true})))))

(defn format-date [date]
  (let [locale (or (current-locale) :en)
        parsed-date* (ch/parse date)
        parsed-date (ch/+ parsed-date*
                          (now/tz-offset))
        same-date? (today? parsed-date)
        en? (= locale :en)]
    (when parsed-date*
      (if same-date?
        (if en?
          (format-us-time parsed-date)
          (ch/format parsed-date (with-meta [:hour ":" :min] {locale true})))
        (ch/format parsed-date (with-meta [[:month :short] \space :day] {locale true}))))))


(defn build-ref [{:keys [id resourceType]}]
  (str resourceType "/" id))

(defn id-from-ref [ref]
  (some->
   ref
   (str/split #"/")
   second))

(defn get-given-name [participant]
  (str/join " " (-> participant :name first :given)))
