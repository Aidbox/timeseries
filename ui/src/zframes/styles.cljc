(ns zframes.styles
  (:require [garden.core :as garden]
            [garden.stylesheet :as stylesheet]))

(defn styles [css]
  [:style (garden/css css)])
