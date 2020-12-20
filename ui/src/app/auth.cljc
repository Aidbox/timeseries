(ns app.auth
  (:require [re-frame.core :as rf]
            [zframes.context :as zc]
            #?(:clj [zframes.utils :as xhr]) ;; hack !!
            #?(:cljs [zframes.xhr :as xhr])
            [clojure.string :as str]))

(rf/reg-cofx
 ::base-url
 (fn [coeffects]
   (assoc coeffects :base-url #?(:clj "http://localhost:8888"
                                 :cljs (or js/BASE_URL "http://localhost:8888")))))

(defn parse-metadata [data]
  (let [user-ref (-> data :roles first :patientId)]
    {:domain     (some-> data :roles first :domain (str/split #"-") first)
     :user-ref   user-ref
     :patient-id (some-> user-ref (str/split #"/") second)}))


(defn build-app-metadata [token]
  (->> token
       (reduce-kv
        (fn [acc k v]
          (if-let [idx (some->
                        (re-find #"custom:app_metadata_(\d+)" (name k))
                        second
                        #?(:cljs js/parseInt
                           :clj  Integer/parseInt))]
            (assoc acc idx v)
            acc)) {})
       sort
       (map second)
       (str/join "")
       str/trim))

(defn parse-token [token]
  nil)

(rf/reg-event-fx
 ::context
 [(rf/inject-cofx ::base-url)]
 (fn [{:keys [base-url db]} [_ {{token :access-token} :search :as params}]]
   (merge
    {::zc/done ::context}
    (when token
      (let [user (parse-token token)
            {:keys [patient-id domain user-ref]} (parse-metadata (:custom:app_metadata_0 user))]
        {:db (assoc db :user (assoc user :patient-id patient-id :user-ref user-ref))
         :dispatch [::xhr/init {:token    token
                                :headers  {"X-Client-Name" domain}
                                :base-url base-url}]})))))


(defn get-user [db & _]
  (:user db))

(rf/reg-sub
 ::user
 get-user)


(rf/reg-cofx
 ::user
 (fn [coeffects]
   (assoc coeffects :user (-> coeffects :db get-user))))
