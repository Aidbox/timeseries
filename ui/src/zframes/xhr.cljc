(ns zframes.xhr
  (:require [clojure.string :as str]
            [re-frame.db :as db]
            [re-frame.core :as rf]))

(defn sub-query-by-spaces
  [k s] (->> (str/split s #"\s+")
             (mapv (fn [v] (str (name k) "=" v)))
             (str/join "&")))

(defn to-query [params]
  (->> params
       (reduce-kv (fn [acc k v]
                    (if (or (nil? v) (str/blank? v))
                      acc
                      (assoc acc k v)))
                  {})
       (mapcat (fn [[k v]]
                 (cond
                   (vector? v) (mapv (fn [vv] (str (name k) "=" vv)) v)
                   (set? v) [(str (name k) "=" (str/join "," v))]
                   :else [(str (name k) "=" v) #_(sub-query-by-spaces k v)])))
       (str/join "&")))

(defn base-url [db url]
  (str (get-in db [:config :base-url]) url))

(defn make-form-data [files]
  #?(:cljs
     (let [form-data (js/FormData.)]
       (doall
        (for [[i file] (map-indexed vector files)]
          (.append form-data (str "file" i) file)))
       form-data)
     :clj nil))

(defonce abort-controller-cache (atom {}))

(defn get-abort-controller [req-id]
  #?(:cljs (do
             (when-let [ctrl (get @abort-controller-cache req-id)]
               (.abort ctrl))
             (swap! abort-controller-cache assoc req-id (js/AbortController.))
             (get @abort-controller-cache req-id))
     :clj nil))

(defn xhr-config [db & _]
  (get-in db [:xhr :config]))

(defn *json-fetch [{:keys [req-id uri format headers  params success error] :as opts}]
  #?(:cljs
    (let [{:keys [token base-url] :as config} (xhr-config @db/app-db)
          abort-controller (when req-id (get-abort-controller req-id))
          fmt (or (get {"json" "application/json" "yaml" "text/yaml"} format) "application/json")
          headers (cond-> (merge {"accept"        fmt
                                  "referer" "http://google.com"
                                  "authorization" (str "Bearer " token)}
                                 (:headers config))
                    (or (nil? token) (str/blank? token)) (dissoc "authorization")
                    (nil? (:files opts))                 (assoc "Content-Type" "application/json")
                    true                                 (merge (or headers {})))
          fetch-opts (-> (merge {:method "get" :mode "cors"
                                 :referrer ""}
                                (when abort-controller {:signal (.-signal abort-controller)})
                                opts)
                         (dissoc :uri :headers :success :error :params :files)
                         (assoc :headers headers))
          fetch-opts (cond-> fetch-opts
                       (:body opts) (assoc :body (if (string? (:body opts)) (:body opts) (.stringify js/JSON (clj->js (:body opts)))))
                       (:files opts) (assoc :body (make-form-data (:files opts))))
          url (str base-url uri)]

      (when req-id
        (rf/dispatch [:xhr/start req-id]))
      (->
       (js/fetch (str url (when params (str "?" (to-query params)))) (clj->js fetch-opts))
       (.then
        (fn [resp]
          (if  (= 500 (.-status resp))
            (throw resp)
            (let [headers (.-headers resp)
                  headers-map (into {} (map vec) (es6-iterator-seq (.entries headers )))
                  content-type (get headers-map "content-type")
                  text? (str/includes? content-type "text/html")]
              (if (or (:dont-parse opts) text?)
                (.then (.text resp)
                       (fn [doc]
                         (let [e (if (<= (.-status resp) 299) success error)]
                           (rf/dispatch [(:event e) (merge e {:request opts, :data doc}) (:params e)])))
                       ;; No json
                       (fn [doc]
                         (println "Error:" doc)
                         (rf/dispatch [(:event success) (merge success {:request opts :data doc})])))
                (.then (.json resp)
                       (fn [doc]
                         (let [data (js->clj doc :keywordize-keys true)
                               status (.-status resp)
                               success? (< status 299)]
                           (->> [(when req-id
                                   (if success?
                                     [:xhr/done req-id {:request opts :data data :status status}]
                                     [:xhr/done req-id {:request opts :error data :status status}]))

                                 (when (and req-id (not success?))
                                   [:xhr/error data])

                                 (when-let [e (if success? success error)]
                                   [(:event e) {:request opts :data data :status status} (:params e)])
                                 (when (and (not success?))
                                   ;; Handle backend erros and 401
                                   )]
                                (mapv #(when % (rf/dispatch %))))))
                       ;; No json
                       (fn [doc]
                         (println "Error:" doc)
                         (rf/dispatch [(:event success) (merge success {:request opts :data doc})]))))))))

       (.catch (fn [err]
                 (prn "->> ------------------------ " err)
                 (when-not (= (.-name err) "AbortError")
                   (when req-id
                     (rf/dispatch [:xhr/done req-id {:request opts :data err}])
                     (rf/dispatch [:xhr/error (.-message err)]))
                   ;; Handler http errors
                   (when (:event error)
                     (rf/dispatch [(:event error) (merge error {:request opts :error err})])))))))
    :clj nil))


(defn json-fetch [opts]
  (if (vector? opts)
    (doseq [o opts] (*json-fetch o))
    (*json-fetch opts)))

(rf/reg-fx :json/fetch json-fetch)

(rf/reg-event-fx
 :xhr/start
 (fn [{db :db} [_ req-id]]
   {:db (assoc-in db [:xhr :req req-id :loading] true)}))

(rf/reg-event-fx
 :xhr/done
 (fn [{db :db} [_ req-id {:keys [request data status] :as resp}]]
   {:db (assoc-in db [:xhr :req req-id] (assoc resp :loading false))}))

(defn get-response [db req-id]
  (get-in db [:xhr :req req-id]))

(rf/reg-sub
 :xhr/response
 (fn [db [_ req-id]]
   (get-response db req-id)))

(rf/reg-event-fx
 :xhr/error
 (fn [{db :db} [_ data]]
   {:view/show-error (or (:message data) data)
    :db (assoc-in db [:global :loading] false)}))

(rf/reg-event-db
 ::init
 (fn [db [_ config]]
   (assoc-in db [:xhr :config] config)))

(rf/reg-sub ::config xhr-config)
