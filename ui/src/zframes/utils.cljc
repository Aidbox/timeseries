(ns zframes.utils
  (:require [clojure.string :as str]))

(defn keywordize [x]
  #?(:cljs (js->clj x :keywordize-keys true)))

(defn endcode-json [val]
  #?(:clj val
     :cljs (->> val
                clj->js
                (.stringify js/JSON)
                js/encodeURIComponent)))

(defn decode-json [json]
  #?(:clj val
     :cljs (->> json
                js/decodeURIComponent
                (.parse js/JSON)
                (keywordize))))

(defn deep-merge
  "efficient deep merge"
  [a b]
  (loop [[[k v :as i] & ks] b
         acc a]
    (if (nil? i)
      acc
      (let [av (get a k)]
        (if (= v av)
          (recur ks acc)
          (recur ks (if (and (map? v) (map? av))
                      (assoc acc k (deep-merge av v))
                      (assoc acc k v))))))))

(defn deep-merge-concat [a b]
  (merge-with
   (fn [x y]
     (cond (map? y)        (deep-merge x y)
           (sequential? y) (into x y)
           :else           y))
   a b))


(defn to-dispatch [req ev]
  (update-in req [:fx :dispatch-n] conj ev))

(defn to-dispatch-n [req ev]
  (update-in req [:fx :dispatch-n] concat ev))

(defn parse-params [s]
  (if (str/blank? s)
    {}
    (reduce
     (fn [acc pair]
       (let [[k v] (str/split pair #"=" 2)]
         (assoc acc (keyword k) #?(:cljs (js/decodeURIComponent v)
                                   :clj v))))
     {} (-> (str/replace s #"^\?" "")
            (str/split "&")))))

(defn parse-hash [fragment]
  (let [[path params-str] (-> fragment
                              (str/replace #"^#" "")
                              (str/split #"\?"))
        params  (if (str/blank? params-str)
                  {}
                  (parse-params (or params-str "")))]
    {:hash-path path
     :hash-query-string params-str
     :hash-params params}))

(defn parse-search [search]
  (let [[_ params-str] (str/split (or search "") #"\?")
        params  (when-not (str/blank? params-str)
                  (parse-params  params-str))]
    {:search-params params}))

(defn wrap-request [{:keys [hash search] :as req}]
  (cond->  (assoc req :dispatch-n []) 
    hash   (merge (parse-hash hash))
    search (merge (parse-search search))))
