(ns zframes.message
  (:require [re-frame.core :as rf]))

(defn postMessage [data]
  #?(:cljs
     (if-let [messenger (if (aget js/window "messageHandler")
                            ;; Android
                            (aget js/window "messageHandler")
                            ;; IOS
                            (when (aget js/window "webkit" )
                              (aget js/window "webkit" "messageHandlers" "narus")))]
       (.postMessage messenger (js/JSON.stringify (clj->js data)))
       (.log js/console "Does not any message handler"))
     :clj data))

(rf/reg-fx ::postMessage postMessage)

(rf/reg-event-fx
 ::postMessage
 (fn [_ [_ message]]
   (postMessage message)))

(defn receiveMessage [event]
  (let [event #?(:cljs (js->clj (js/JSON.parse (aget event "data")) :keywordize-keys true)
                 :clj event)]
    (rf/dispatch [(keyword (str "app.messages/" (:ev event)))  event])))

#?(:cljs
   (.addEventListener js/window "message" receiveMessage false ))
