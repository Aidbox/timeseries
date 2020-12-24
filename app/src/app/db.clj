(ns app.db
  (:import (com.zaxxer.hikari HikariConfig HikariDataSource)
           (java.util Properties))
  (:require [clojure.string :as str]))

(def defaults
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
   :validation-timeout 5000
   :idle-timeout       600000
   :max-lifetime       1800000
   :minimum-idle       10
   :maximum-pool-size  10})

(defn upcase [s]
  (str
   (.toUpperCase (.substring s 0 1))
   (.substring s 1)))

(defn propertize [k]
  (let [parts (str/split (name k) #"-")]
    (str (first parts) (str/join "" (map upcase (rest parts))))))

(defn create-pool [opts]
  (let [props (Properties.)]
    (.setProperty props "dataSourceClassName" "org.postgresql.ds.PGSimpleDataSource")
    (doseq [[k v] (merge defaults opts)]
      (when (and k v)
        (.setProperty props (propertize k) (str v))))
    (-> props
        HikariConfig.
        HikariDataSource.)))

(defn close-pool [datasource] (.close datasource))

(defn database-url [spec]
  (let [conn spec]
    (str "jdbc:postgresql://" (:host conn) ":" (:port conn)
         "/" (:database conn)
         "?user=" (:user conn)
         "&password=" (:password conn) "&stringtype=unspecified")))

(defn datasource [{pool-spec :pool :as  spec}]
  (let [ds-opts   (let [database-url (database-url spec)]
                    (merge {:connection-timeout  30000
                            :idle-timeout        10000
                            :minimum-idle        0
                            :maximum-pool-size   5
                            :connection-init-sql "select 1"
                            :data-source.url     database-url}
                           pool-spec))
        ds (create-pool ds-opts)]
    {:datasource ds}))
