(ns app.pages.index.view
  (:require [zframes.pages :as pages]
            [re-frame.core :as rf]
            [app.pages.index.model :as model]))

(pages/reg-subs-page
 model/index-page
 (fn [page _]
   [:div.sidebar
    [:div.sidebar-inner
     [:div.sidebar-logo
      [:div.peers.ai-c.fxw-nw
       [:div.peer.peer-greed
        [:a.sidebar-link.td-n
         {:href "https://colorlib.com/polygon/adminator/index.html"}]
        [:div.peers.ai-c.fxw-nw
         [:div.peer
          #_[:div.logo
           [:img {:alt "", :src "assets/static/images/logo.png"}]]]
         [:div.peer.peer-greed [:h5.lh-1.mB-0.logo-text "Aidbox ROCKS"]]]]
       [:div.peer
        [:div.mobile-toggle.sidebar-toggle
         [:a.td-n {:href ""} [:i.ti-arrow-circle-left]]]]]]
     [:ul.sidebar-menu.scrollable.pos-r.ps
      [:li.nav-item.mT-30.active
       [:a.sidebar-link
        {:default "",
         :href "https://colorlib.com/polygon/adminator/index.html"}
        [:span.icon-holder [:i.c-blue-500.ti-home] " "]
        [:span.title "Dashboard"]]]
      [:li.nav-item
       [:a.sidebar-link
        {:href "email.html"}
        [:span.icon-holder [:i.c-brown-500.ti-email] " "]
        [:span.title "Email"]]]






      [:li.nav-item.dropdown
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-orange-500.ti-layout-list-thumb] " "]
        [:span.title "Tables"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li
         [:a.sidebar-link {:href "basic-table.html"} "Basic Table"]]
        [:li [:a.sidebar-link {:href "datatable.html"} "Data Table"]]]]
      [:li.nav-item.dropdown
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-purple-500.ti-map] " "]
        [:span.title "Maps"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li [:a {:href "google-maps.html"} "Google Map"]]
        [:li [:a {:href "vector-maps.html"} "Vector Map"]]]]
      [:li.nav-item.dropdown
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-red-500.ti-files] " "]
        [:span.title "Pages"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li [:a.sidebar-link {:href "404.html"} "404"]]
        [:li [:a.sidebar-link {:href "500.html"} "500"]]
        [:li [:a.sidebar-link {:href "signin.html"} "Sign In"]]
        [:li [:a.sidebar-link {:href "signup.html"} "Sign Up"]]]]
      [:li.nav-item.dropdown
       [:a.dropdown-toggle
        {:href "javascript:void(0);"}
        [:span.icon-holder [:i.c-teal-500.ti-view-list-alt] " "]
        [:span.title "Multiple Levels"]
        " "
        [:span.arrow [:i.ti-angle-right]]]
       [:ul.dropdown-menu
        [:li.nav-item.dropdown [:a {:href "javascript:void(0);"} [:span "Menu Item"]]]
        [:li.nav-item.dropdown
         [:a
          {:href "javascript:void(0);"}
          [:span "Menu Item"]
          " "
          [:span.arrow [:i.ti-angle-right]]]
         [:ul.dropdown-menu
          [:li [:a {:href "javascript:void(0);"} "Menu Item"]]
          [:li [:a {:href "javascript:void(0);"} "Menu Item"]]]]]]]
     #_[:div.ps__rail-x
      {:style "left: 0px; bottom: 0px;"}
      [:div.ps__thumb-x
       {:style "left: 0px; width: 0px;", :tabindex "0"}]]
     #_[:div.ps__rail-y
      {:style "top: 0px; right: 0px;"}
      [:div.ps__thumb-y
       {:style "top: 0px; height: 0px;", :tabindex "0"}]]]]

   ))
