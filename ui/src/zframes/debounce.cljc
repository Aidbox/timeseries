(ns zframes.debounce
  #?(:cljs (:import [goog.async Debouncer])))

(defn debounce [f ev ms]
  #?(:clj  (fn [& args] (ev (apply f args)))
     :cljs (let [debouncer (Debouncer. ev (or ms 400))]
             (fn [& args] (.fire debouncer (apply f args))))))
