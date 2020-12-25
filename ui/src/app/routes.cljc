(ns app.routes)

(def routes
  {:.          :app.pages.index.model/index-page
   :layout     :main
   "patient"   {:. :app.pages.patient.model/index-page
                :layout     :main
                [:id] {:layout     :main
                       :. :app.pages.patient.model/view-page}}})
