(ns zframes.cookies
  (:require [re-frame.core :as rf]
            [zframes.utils :as u]
            #?(:cljs [goog.net.cookies :as gcookies])
            #?(:cljs [cljs.reader :as reader])))

#?(:clj (defonce cookies (atom {})))

(defn get-cookie 
  [k]
  #?(:clj (get cookies @k)
     :cljs (-> k
               name
               .get goog.net.cookies
               u/decode-json)))

(defn set-cookie
  [k v]
  #?(:clj  (swap! cookies assoc k v)
     :cljs (.set goog.net.cookies (name k)
                 (u/encode-json v))))

(defn remove-cookie
  [k]
  #?(:clj (swap! cookies dissoc k)
     :cljs (.remove goog.net.cookies (name k))))

(rf/reg-cofx
 ::get
 (fn [coeffects key]
   (assoc-in coeffects [:cookie key] (get-cookie key))))

(rf/reg-fx
 ::set
 (fn [{k :key v :value}]
   (set-cookie k v)))

(rf/reg-fx ::remove remove-cookie)
