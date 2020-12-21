(ns zframes.layout)

(defmulti layout
  (fn [req]
    (get-in req [:route :layout])))

(defmethod layout
  :default
  [req]
  (fn [req resp]
    [:div resp]))
