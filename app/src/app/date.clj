(ns app.date
  (:require [clojure.string :as str]))

(defn parse-date [date]
  (cond
    (re-find #"^(19|20)\d\d([-])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"-"))]
      (zipmap [:year :month :day :splitter] (conj date-elements "-")))

    (re-find #"^(0[1-9]|[12][0-9]|3[01])[-](0[1-9]|1[012])[-](19|20)\d\d$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"-"))]
      (zipmap [:day :month :year :splitter] (conj date-elements "-")))

    (re-find #"^(19|20)\d\d([.])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"\."))]
      (zipmap [:year :month :day :splitter] (conj date-elements ".")))

    (re-find #"^(0[1-9]|[12][0-9]|3[01])[.](0[1-9]|1[012])[.](19|20)\d\d$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"\."))]
      (zipmap [:day :month :year :splitter] (conj date-elements ".")))

    (re-find #"^(19|20)\d\d([/])(0[1-9]|1[012])\2(0[1-9]|[12][0-9]|3[01])$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"."))]
      (zipmap [:year :month :day :splitter] (conj date-elements ".")))

    (re-find #"^(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/](19|20)\d\d$" date)
    (let [date-elements (mapv #(Integer/parseInt %) (str/split date #"/"))]
      (zipmap [:day :month :year :splitter] (conj date-elements "/")))))

(defn rand-date
  ([base]
   (let [current-year (-> (java.time.LocalDate/now) .getYear .toString Integer/parseInt inc)
         parsed-base (parse-date base)
         rnd-year (+ (:year parsed-base) (rand-int (- current-year (:year parsed-base))))
         rnd-month (+ 1 (rand-int 12))
         rnd-day (cond
                   (and (zero? (mod current-year 4)) (= rnd-month 2))
                   (+ 1 (rand-int 29))
                   (= rnd-month 2)
                   (+ 1 (rand-int 28))
                   :else
                   (+ 1 (rand-int 31)))]
     (str/join (:splitter parsed-base) (-> parsed-base
                                           (assoc
                                            :year rnd-year
                                            :day (if (< rnd-day 10)
                                                   (str "0" rnd-day)
                                                   rnd-day)
                                            :month (if (< rnd-month 10)
                                                     (str "0" rnd-month)
                                                     rnd-month))
                                           vals
                                           butlast))))
  ([p-start p-end]
   (let [p-start (parse-date p-start)
         p-end (parse-date p-end)
         rnd-year (if (not (= (:year p-start) (:year p-end)))
                    (+ (:year p-start) (rand-int (inc (- (:year p-end) (:year p-start)))))
                    (:year p-start))
         rnd-month (cond
                     (and (= (:month p-start) (:month p-end)) (= (:year p-start) (:year p-end)))
                     (:month p-start)
                     (= (:year p-start) (:year p-end))
                     (+ (:month p-start) (rand-int (inc (- (:month p-end) (:month p-start)))))
                     :else
                     (+ 1 (rand-int 12)))
         rnd-day (cond
                   (and (zero? (mod rnd-year 4)) (= rnd-month 2))
                   (+ (:day p-start) (rand-int 30))
                   (= rnd-month 2)
                   (+ 1 (rand-int 28))
                   :else
                   (+ 1 (rand-int 31)))]
     (str/join (:splitter p-start) (-> p-start
                                           (assoc
                                            :year rnd-year
                                            :day (if (< rnd-day 10)
                                                   (str "0" rnd-day)
                                                   rnd-day)
                                            :month (if (< rnd-month 10)
                                                     (str "0" rnd-month)
                                                     rnd-month))
                                           vals
                                           butlast)))))
