(ns zframes.storage
  (:require [re-frame.core :as rf]
            [zframes.utils :as u]))

(defn keywordize [x]
  (js->clj x :keywordize-keys true))

(defn remove-item
  [key]
  (.removeItem (.-localStorage js/window) key))

(defn set-item
  [key val]
  (->> val
       u/encode-json
       (.setItem (.-localStorage js/window) (name key))))

(defn get-item
  [key]
  (try (->> key
            name
            (.getItem (.-localStorage js/window))
            u/decode-json)
       (catch js/Object e (do (remove-item key) nil))))

(rf/reg-cofx
 ::get
 (fn [coeffects keys]
   (reduce (fn [coef k]
             (assoc-in coef [:storage k] (get-item k)))
           coeffects keys)))

(rf/reg-fx
 ::set
 (fn [items]
   (doseq [[k v] items]
     (set-item k v))))

(rf/reg-fx
 ::remove
 (fn [keys]
   (doseq [k keys]
     (remove-item (name k)))))

