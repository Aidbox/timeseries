(ns app.layout
  (:require [zframes.layout :as zl]
            [zframes.styles :as s]
            [zframes.routing :as zr]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [app.auth :as auth]))

(def app-styles
  (s/styles
   []))

(def sidebar
  [:div.sidebar
    [:div.sidebar-inner
     [:div.sidebar-logo {:style {:height "65px"}}
      [:div.peers.ai-c.fxw-nw
       [:div.peer.peer-greed
        [:a.sidebar-link.td-n
         {:href "https://colorlib.com/polygon/adminator/index.html"}]
        [:div.peers.ai-c.fxw-nw
         [:div.peer
          [:div.logo
           [:img {:width "50px" :height "50px" :alt "", :src "/heart.svg"}]]]
         [:div.peer.peer-greed [:h5.ml-4.lh-1.mB-0.logo-text {:style {:color "#313435" :font-size "24px" :font-weight "300"}} "Aidbox & TS"]]]]
       [:div.peer
        [:div.mobile-toggle.sidebar-toggle
         [:a.td-n {:href ""} [:i.ti-arrow-circle-left]]]]]]
     [:ul.sidebar-menu.scrollable.pos-r.ps
      [:li.nav-item.mT-30.active
       [:a.sidebar-link
        {:default "",
         :href "#/"}
        [:span.icon-holder [:i.c-blue-500.ti-home] " "]
        [:span.title "Patients"]]]

      [:li.nav-item.dropdown.open
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-orange-500.ti-layout-list-thumb] " "]
        [:span.title "Alerts"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li [:a {:href "#/"} "Heart Rate "
                  [:span.d-ib.lh-0.va-m.fw-600.bdrs-10em.pX-10.pY-10.bgc-red-50.c-red-500 "4"]]]
        [:li [:a {:href "#/"} "Oxygen saturation "
              [:span.d-ib.lh-0.va-m.fw-600.bdrs-10em.pX-10.pY-10.bgc-green-50.c-green-500 "10"]]]]]

      [:li.nav-item.dropdown.open
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-teal-500.ti-view-list-alt] " "]
        [:span.title "Procedures"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li.nav-item.dropdown
         [:a {:href "javascript:void(0);"} [:span "ECG"]]]
        [:li.nav-item.dropdown
         [:a {:href "javascript:void(0);"} [:span "EEG"] " "]]]]]]])


(def header
  [:div.header.navbar
   [:div.header-container
    [:ul.nav-left
     [:li
      [:a#sidebar-toggle.sidebar-toggle
       {:href "javascript:void(0);"}
       [:i.ti-menu]]]
     [:li.search-box
      [:a.search-toggle.no-pdd-right
       {:href "javascript:void(0);"}
       [:i.search-icon.ti-search.pdd-right-10]
       " "
       [:i.search-icon-close.ti-close.pdd-right-10]]]
     [:li.search-input
      [:input.form-control {:placeholder "Search...", :type "text"}]]]
    [:ul.nav-right
     [:li.notifications.dropdown
      [:span.counter.bgc-red "3"]
      " "
      [:a.dropdown-toggle.no-after
       {:data-toggle "dropdown", :href ""}
       [:i.ti-bell]]
      [:ul.dropdown-menu
       [:li.pX-20.pY-15.bdB
        [:i.ti-bell.pR-10]
        " "
        [:span.fsz-sm.fw-600.c-grey-900 "Notifications"]]
       [:li
        [:ul.ovY-a.pos-r.scrollable.lis-n.p-0.m-0.fsz-sm.ps
         [:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/1.jpg"}]]
          [:div.peer.peer-greed
           [:span
            [:span.fw-500 "John Doe"]
            " "
            [:span.c-grey-600 "liked your " [:span.text-dark "post"]]]
           [:p.m-0 [:small.fsz-xs "5 mins ago"]]]]
         [:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/2.jpg"}]]
          [:div.peer.peer-greed
           [:span
            [:span.fw-500 "Moo Doe"]
            " "
            [:span.c-grey-600
             "liked your "
             [:span.text-dark "cover image"]]]
           [:p.m-0 [:small.fsz-xs "7 mins ago"]]]]
         [:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/3.jpg"}]]
          [:div.peer.peer-greed
           [:span
            [:span.fw-500 "Lee Doe"]
            " "
            [:span.c-grey-600
             "commented on your "
             [:span.text-dark "video"]]]
           [:p.m-0 [:small.fsz-xs "10 mins ago"]]]]]
        #_[:div.ps__rail-x
         {:style "left: 0px; bottom: 0px;"}
         [:div.ps__thumb-x
          {:style "left: 0px; width: 0px;", :tabindex "0"}]]
        #_[:div.ps__rail-y
         {:style "top: 0px; right: 0px;"}
         [:div.ps__thumb-y
          {:style "top: 0px; height: 0px;", :tabindex "0"}]]]]]
     [:li.pX-20.pY-15.ta-c.bdT
      [:span
       [:a.c-grey-600.cH-blue.fsz-sm.td-n
        {:href ""}
        "View All Notifications "
        [:i.ti-angle-right.fsz-xs.mL-10]]]]]
    #_[:ul
     [:li.notifications.dropdown
      [:span.counter.bgc-blue "3"]
      " "
      [:a.dropdown-toggle.no-after
       {:data-toggle "dropdown", :href ""}
       [:i.ti-email]]
      [:ul.dropdown-menu
       [:li.pX-20.pY-15.bdB
        [:i.ti-email.pR-10]
        " "
        [:span.fsz-sm.fw-600.c-grey-900 "Emails"]]
       [:li
        [:ul.ovY-a.pos-r.scrollable.lis-n.p-0.m-0.fsz-sm.ps
         [:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/1.jpg"}]]
          [:div.peer.peer-greed
           [:div
            [:div.peers.jc-sb.fxw-nw.mB-5
             [:div.peer [:p.fw-500.mB-0 "John Doe"]]
             [:div.peer [:small.fsz-xs "5 mins ago"]]]
            [:span.c-grey-600.fsz-sm
             "Want to create your own customized data generator for your app..."]]]]
         [:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/2.jpg"}]]
          [:div.peer.peer-greed
           [:div
            [:div.peers.jc-sb.fxw-nw.mB-5
             [:div.peer [:p.fw-500.mB-0 "Moo Doe"]]
             [:div.peer [:small.fsz-xs "15 mins ago"]]]
            [:span.c-grey-600.fsz-sm
             "Want to create your own customized data generator for your app..."]]]]
         #_[:li
          [:a.peers.fxw-nw.td-n.p-20.bdB.c-grey-800.cH-blue.bgcH-grey-100
           {:href ""}]
          [:div.peer.mR-15
           [:img.w-3r.bdrs-50p
            {:alt "",
             :src "https://randomuser.me/api/portraits/men/3.jpg"}]]
          [:div.peer.peer-greed
           [:div
            [:div.peers.jc-sb.fxw-nw.mB-5
             [:div.peer [:p.fw-500.mB-0 "Lee Doe"]]
             [:div.peer [:small.fsz-xs "25 mins ago"]]]
            [:span.c-grey-600.fsz-sm
             "Want to create your own customized data generator for your app..."]]]]]
        #_[:div.ps__rail-x
         {:style "left: 0px; bottom: 0px;"}
         [:div.ps__thumb-x
          {:style "left: 0px; width: 0px;", :tabindex "0"}]]
        #_[:div.ps__rail-y
         {:style "top: 0px; right: 0px;"}
         [:div.ps__thumb-y
          {:style "top: 0px; height: 0px;", :tabindex "0"}]]]]]
     ]
    #_[:ul
     [:li.dropdown
      [:a.dropdown-toggle.no-after.peers.fxw-nw.ai-c.lh-1
       {:data-toggle "dropdown", :href ""}]
      [:div.peer.mR-10
       [:img.w-2r.bdrs-50p
        {:alt "",
         :src "https://randomuser.me/api/portraits/men/10.jpg"}]]
      [:div.peer [:span.fsz-sm.c-grey-900 "John Doe"]]
      [:ul.dropdown-menu.fsz-sm
       [:li
        [:a.d-b.td-n.pY-5.bgcH-grey-100.c-grey-700
         {:href ""}
         [:i.ti-settings.mR-10]
         " "
         [:span "Setting"]]]
       [:li
        [:a.d-b.td-n.pY-5.bgcH-grey-100.c-grey-700
         {:href ""}
         [:i.ti-user.mR-10]
         " "
         [:span "Profile"]]]
       [:li
        [:a.d-b.td-n.pY-5.bgcH-grey-100.c-grey-700
         {:href ""}
         [:i.ti-email.mR-10]
         " "
         [:span "Messages"]]]
       [:li.divider {:role "separator"}]
       [:li
        [:a.d-b.td-n.pY-5.bgcH-grey-100.c-grey-700
         {:href ""}
         [:i.ti-power-off.mR-10]
         " "
         [:span "Logout"]]]]]]]]


  )

(defmethod zl/layout :main
  [req]
  (let [user (rf/subscribe [::auth/user])]
    (fn [{page :current-page :as req} resp]
      [:div.root.main-layout.grid app-styles
       sidebar
       [:div.page-container
        header

        [:main.main-content.bgc-grey-100 resp]]])))
