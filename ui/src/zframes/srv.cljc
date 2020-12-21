(ns zframes.srv
  (:require [clojure.string :as str]
            [zframes.utils :as u]))

(defn js-sanitize-location
  [location]
  #?(:cljs
     (let [keys [:hash :host :hostname :href :origin :pathname :port :protocol :search]]
       (zipmap keys (map #(aget location (name %)) keys)))))

(defn make-request []
  (let [req #?(:cljs (js-sanitize-location js/window.location)
               :clj {})]
    (u/wrap-request req)))

(defn call-handler [handler]
  (fn []
    (handler (make-request))))

;; Fix re closure on develop hotreload
(defonce js-pushState #?(:cljs (aget js/history "pushState")
                         :clj {}))

(defn patch-pushstate [history on-uri-change]
  #?(:cljs
     (aset history "pushState"
           (fn [& args]
             (.apply js-pushState history (clj->js args))
             (on-uri-change)))))


(defn run [handler]
  #?(:cljs
     (let [on-uri-change (call-handler handler)]
       (patch-pushstate  js/window.history on-uri-change)
       (aset js/window "onpopstate" on-uri-change)
       ;; Retun srv instance
       on-uri-change)

     :clj (call-handler handler)))
