(ns app.db
  (:import (com.zaxxer.hikari HikariConfig HikariDataSource)
           (java.util Properties)

           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           (org.joda.time DateTime)
           java.time.format.DateTimeFormatter
           java.time.ZoneOffset
           [java.sql
            BatchUpdateException
            Date
            Timestamp
            PreparedStatement]
           [org.postgresql.jdbc PgArray]
           org.postgresql.util.PGobject)
  (:require [clojure.string :as str]
            [clojure.java.jdbc :as jdbc]
            [clj-time.coerce :as tc]
            [honeysql.format :as sqlf]
            [clj-time.core :as t]))

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


(def time-fmt
  (->
   (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss")
   (.withZone (java.time.ZoneOffset/UTC))))


(defn- to-date [sql-time]
  (str (.format time-fmt (.toInstant sql-time)) "." (format "%06d"  (/ (.getNanos sql-time) 1000)) "Z"))

(defn- to-sql-date [clj-time]
  (tc/to-sql-time clj-time))


(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))


(extend-protocol jdbc/IResultSetReadColumn
  Date
  (result-set-read-column [v _ _] (.toString v))

  Timestamp
  (result-set-read-column [v _ _]
    (.toString (.toInstant v))))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))


(extend-protocol jdbc/ISQLValue
  clojure.lang.Keyword
  (sql-value [value] (name value))
  org.joda.time.DateTime
  (sql-value [value] (to-sql-date value))
  java.util.Date
  (sql-value [value] (java.sql.Timestamp. (.getTime value))))

(defmethod sqlf/format-clause :returning [[_ fields] sql-map]
  (str "RETURNING "
       (when (:modifiers sql-map)
         (str (sqlf/space-join (map (comp clojure.string/upper-case name)
                                    (:modifiers sql-map)))
              " "))
       (sqlf/comma-join (map sqlf/to-sql fields))))
