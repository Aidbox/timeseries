(ns zframes.context
  (:require [zframes.utils :as u]
            [clojure.core.async :as a]
            [re-frame.core :as rf]))

(defn context-idx
  "Associate existing routes with
  array of all parent contexts and params"
  ([route]
   (context-idx route [] []))
  ([{page :. context :context :as route} ctx params]
   (when page
     (let [ctx (cond-> ctx
                 context (conj {:context context
                                :params {:route params}}))]
       (reduce-kv
        (fn [acc k v]
          (->> (cond-> params
                 (vector? k) (conj (first k)))
               (context-idx v ctx)
               (merge acc)))
        (hash-map page ctx)
        route)))))

(defn template
  "Insert into context params from route and search params"
  [ctx {{:keys [search route]} :params :as current-page}]
  (-> ctx
      (assoc-in  [:params :search] search)
      (update-in [:params :route]  #(select-keys route %))))

(defn template-ctx [idx current-page]
  (let [ctx (get idx (:ev current-page))]
    (mapv #(template % current-page) ctx)))

(defn mk-context-evs [ctx]
  (mapv (fn [{:keys [context params]}] [context params]) ctx))

(defonce chan (a/chan))

(rf/reg-fx
 ::done
 (fn [ctx-id]
   (a/go (a/>! chan ctx-id))))

(defn save-context [db ctx-id data]
  (assoc-in db [:context ctx-id] data))

(defn get-context [db ctx-id]
  (get-in db [:context ctx-id]))

(defn get-all-contexts [db]
  (:context db))

(rf/reg-cofx
 ::context
 (fn
   ([coeffects]
    (assoc coeffects :context (get-in coeffects [:db :context])))
   ([coeffects ctx-id]
    (assoc coeffects :context (get-in coeffects [:db :context ctx-id])))))

(rf/reg-sub
 ::context
 (fn [db [_ ctx-id]]
   (get-in db [:context ctx-id])))

(rf/reg-event-fx
 ::done
 (fn [{db :db} [_ ctx-id data]]
   {:db (save-context db ctx-id data) 
    ::done ctx-id}))

(defn context-diff [old new]
  (->> (map vector new (concat old (repeat nil)))
       (drop-while (partial apply =))
       (map first)))

(defn context-diff-slow [old new]
  (loop [a old, b new]
    (if (= (first a) (first b))
      (recur (rest a) (rest b))
      b)))

(defn context
  [handler route]
  (let [idx (context-idx route)
        state (atom nil)]
    (fn [{page :current-page :as req}]
      (let [ctx (template-ctx idx page)]
        (if (= ctx @state)
          (handler req)
          (let [to-dispatch (context-diff @state ctx)]
            (reset! state ctx)
            (->> to-dispatch
                 mk-context-evs 
                 (assoc req :ctx-fx)
                 handler)))))))


