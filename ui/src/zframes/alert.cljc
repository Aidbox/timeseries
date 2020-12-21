(ns zframes.alert
  (:require [re-frame.core :as rf]
            #?(:cljs [antd-mobile :refer (Modal)])))

(rf/reg-event-fx
 ::alert
 (fn [db [_ alert]]
   {::alert alert}))

(rf/reg-fx
 ::alert
 (fn [{:keys [title body actions] :as alert}]
   #?(:cljs ((.-alert Modal) title body (clj->js actions))
      :clj  alert)))
