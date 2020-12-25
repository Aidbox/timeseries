(ns app.routes)

(def routes
  {:.          :app.pages.index.model/index-page
   :layout     :main
   "devices"   {:. :app.pages.devices.model/index-page
                :layout     :main}
   "patient"   {:. :app.pages.patient.model/index-page
                :layout     :main
                [:id] {:layout     :main
                       :. :app.pages.patient.model/view-page}}
   "alert"     {:layout :main
                [:type] {:layout :main
                         :. :app.pages.alert.model/view-page}}})
