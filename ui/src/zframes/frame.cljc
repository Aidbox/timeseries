(ns zframes.frame
  (:require [re-frame.core :as rf]))

#?(:clj
   (defmacro reg-sub
     [id & args]
     (assert (symbol? id)
             (format "Query id must be type of symbol but % given" (type id)))
     (let [k (keyword (str *ns*) (name id))]
       `(do (def ~id ~k)
            (rf/reg-sub ~k ~@args)))))

#?(:clj
   (defmacro reg-event-fx
     [id & args]
     (assert (symbol? id)
             (format "Event id must be type of symbol but % given" (type id)))
     (let [k (keyword (str *ns*) (name id))]
       `(do (def ~id ~k)
            (rf/reg-event-fx ~k ~@args)))))

#?(:clj
   (defmacro reg-event-db
     [id & args]
     (assert (symbol? id)
             (format "Event id must be type of symbol but % given" (type id)))
     (let [k (keyword (str *ns*) (name id))]
       `(do (def ~id ~k)
            (rf/reg-event-db ~k ~@args)))))
