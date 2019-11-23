(ns clash.render
  (:gen-class)
  (:require [clash.api :as api]
            [clojure.data.json :as json]
            [clojure.walk :as walk]
            [java-time :as jt]
            [hiccup.page :as hu]
            [environ.core :refer [env]]))

(def base-url (env :base-url))

(defn get-datetime
  "Convert special datetime format"
  [s]
  (jt/format "yyyy-MM-dd HH:mm:ss"
             (jt/local-date-time "yyyyMMdd'T'HHmmss.SSS'Z'" s)))

;; json rendering

(defn render-members-json []
  (with-out-str (json/pprint (api/my-clan-members))))

(defn render-war-json []
  (with-out-str (json/pprint (api/my-clan-war))))

(defn render-warlog-json []
  (with-out-str (json/pprint (api/my-clan-warlog))))

;; html rendering

;; members

(def member-mapping [:clanRank "Rank"
                      :name "Name"
                      :tag "Tag"
                      :role "Role"
                      :lastSeen "Last seen"
                      :donations "Donations"
                      :donationsReceived "Donations Received"
                      :expLevel "Exp. Level"
                      :previousClanRank "Previous Rank"
                      :arena "Arena"
                      :clanChestPoints "Clan Chest Pts."])
(def member-keys (take-nth 2 member-mapping))
(def member-map (apply hash-map member-mapping))
(def member-conv {:lastSeen get-datetime
                  :arena :name})
(defn convert [k elem]
  (let [cf (k member-conv)]
    (if cf
      (cf elem)
      elem)))

(defn member-title []
  [:tr (map (fn [k] [:th (k member-map)]) member-keys)])

(defn member [part]
  [:tr (map (fn [k] [:td (convert k (k part))]) member-keys)])

(defn render-members []
  (let [data (:items (walk/keywordize-keys (api/my-clan-members)))]
    (hu/html5
     [:header [:title "Members"] (hu/include-css "style.css")]
     [:body
      [:h1 "Members"]
      [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
       [:table
        (member-title)
        (map member data)]]])))

;; war

(def war-participant-mapping [:name "Name"
                              :tag "Tag"
                              :battlesPlayed "Battles Played"
                              :wins "Wins"
                              :collectionDayBattlesPlayed "Coll. Day Battles Played"
                              :numberOfBattles "No. Battles"
                              :cardsEarned "Cards Earned"])
(def war-participant-keys (take-nth 2 war-participant-mapping))
(def war-participant-map (apply hash-map war-participant-mapping))

(defn get-war-state [data]
  (let [state (:state data)]
    (cond
      (= state "collectionDay") (str "Collection Day - Ends on " (get-datetime (:collectionEndTime data)))
      (= state "warDay") (str "War Day - Ends on " (get-datetime (:warEndTime data)))
      :default state)))

(defn war-participant-title []
  [:tr (map (fn [k] [:th (k war-participant-map)]) war-participant-keys)])

(defn war-participant [part]
  [:tr (map (fn [k] [:td (k part)]) war-participant-keys)])

(defn render-war []
  (let [data (walk/keywordize-keys (api/my-clan-war))
        war-state (get-war-state data)]
    (hu/html5
     [:header [:title "Current War"] (hu/include-css "style.css")]
     [:body
      [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
       [:table
        [:h1 "Current War"]
        [:h2 war-state]
        (war-participant-title)
        (map war-participant (:participants data))]]])))

;; warlog (participant mapping is the same as the war)

(defn render-one-log [data]
  (let [war-season (:seasonId data)
        war-time (get-datetime (:createdDate data))]
    (list
     [:h2 (str "Season " war-season " - War Creation Time: " war-time)]
     [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
      [:table
       (war-participant-title)
       (map war-participant (:participants data))]])))

(defn render-warlog []
  (let [logs (:items (walk/keywordize-keys (api/my-clan-warlog)))]
    (hu/html5
     [:header [:title "War Log"] (hu/include-css "style.css")]
     [:body
      [:h1 "War Log"]
      (map render-one-log logs)])))

;; home
(defn render-home []
  (hu/html5
   [:header [:title "Home - CR Stats"] (hu/include-css "style.css")]
   [:body
    [:h1 "Home - CR Stats"]
    [:ul
     [:li [:a {:href (str base-url "/members")} "Members"]]
     [:li [:a {:href (str base-url "/war")} "Current War"]]
     [:li [:a {:href (str base-url "/warlog")} "War Logs"]]]]))

;; not found

(defn render-not-found []
  (hu/html5
   [:header [:title "Page Not Found"] (hu/include-css "style.css")]
   [:body
    [:h1 "Page Not Found"]]))
