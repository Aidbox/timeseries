(ns app.pages.alert.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [clojure.string :as str]
            [goog.string :as gstring]
            [app.pages.alert.model :as model]))

(defn ->local-date [date]
  (.toLocaleString (js/Date. date)))

(defn ->local-time [date]
  (.toLocaleTimeString (js/Date. date)))

(defn ->ts [date]
  (.getTime (js/Date. date)))

(defn format-avg [num]
  (gstring/format "%.1f" num))

(pages/reg-subs-page
 model/view-page
 (fn [{:keys [alerts]} req]
   [:div.row
    [:div.col-md-12
     [:div.bd.bgc-white
      (for [[p-id results] alerts]
        (let [type (get-in req [:params :route :type])
              view-name (str type "_view")
              results* (sort-by :time results)
              start    (:time (first results*))
              end      (:time (last results*))
              avgs     (map :avg results*)
              from     (apply min avgs)
              to       (apply max avgs)]
          [:a {:key  p-id
               :href (str "#/patient/" p-id)}
           [:div.pX-30.pY-20.peers.ai-c.bdB.bgcH-grey-100.cur-p
            [:div.peer.mR-20
             [:img.bdrs-50p.w-3r.h-3r {:src (str "https://i.pravatar.cc/150?u=" p-id)}]]

            [:div {:style
                   {:padding          "10px"
                    :border-radius    "18px"
                    :background-color "#ffebee"
                    :color            "#ff3c7e"
                    :font-weight      "600"}}
             [:div "Range: " (format-avg from) " - " (format-avg to)]
             [:div "Period: " (->local-time start) " - " (->local-time end)]]]
           [:div.layer.w-100.fxg-1.bgc-grey-200.scrollable.pos-r.ps
            [:div.p-20.gapY-15
             [:div.peers.fxw-nw
              [:div.peer.peer-greed
               [:div.ai-fs.gapY-5.layers.bgc-grey-200 {:style {:display        "flex"
                                                               :flex-direction "row"
                                                               :flex-wrap      "wrap"}}
                [:iframe  {:src          (str "http://localhost:3000/d-solo/rb_qDpxGz/new-dashboard-copy1?orgId=1&var-Patient=" p-id "&var-View=" view-name "&from=now-10m&to=now&panelId=11")
                           :width        "100%"
                           :height       "200"
                           :frameborder= "0"
                           :style        {:border :none}}]]]]]]]))]]]))
