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

;;;; json rendering

(defn render-json [fun]
  (with-out-str (json/pprint (fun))))

(defn render-members-json []
  (render-json api/my-clan-members))

(defn render-war-json []
  (render-json api/my-clan-war))

(defn render-warlog-json []
  (render-json api/my-clan-warlog))

;;;; html rendering

;; topnav

(defn topnav [active]
  [:div {:class "topnav"}
    [:a {:class (if (= active :home) "active" "normal") :href (str base-url "/")} "Home"]
    [:a {:class (if (= active :members) "active" "normal") :href (str base-url "/members")} "Members"]
    [:a {:class (if (= active :war) "active" "normal") :href (str base-url "/war")} "War"]
    [:a {:class (if (= active :warlog) "active" "normal") :href (str base-url "/warlog")} "Warlogs"]])

;; generic helpers

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn header [title]
  [:header [:title title] (hu/include-css "style.css") (hu/include-js "sortable.js")])

(defn viewport []
  [:meta {"name" "viewport" "content" "width=device-width, initial-scale=1.0"}])

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

(defn member-on-click-action [k tableId]
  (str "sortTable(" (.indexOf member-keys k) "," tableId ")"))

(defn member-title [tableId]
  [:tr (map (fn [k] [:th {"onclick" (member-on-click-action k tableId)} (k member-map)]) member-keys)])

(defn member [part]
  [:tr (map (fn [k] [:td (convert k (k part))]) member-keys)])

(defn render-members []
  (let [data (:items (walk/keywordize-keys (api/my-clan-members)))
        tableId 0]
    (hu/html5
     (header "Members")
     [:body
      (viewport)
      (topnav :members)
      [:h1 "Members"]
      [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
       [:table {"id" tableId}
        (member-title tableId)
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

(defn war-on-click-action [k tableId]
  (str "sortTable(" (.indexOf war-participant-keys k) "," tableId ")"))

(defn war-participant-title [tableId]
  [:tr (map (fn [k] [:th {"onclick" (war-on-click-action k tableId)} (k war-participant-map)])
            war-participant-keys)])

(defn war-participant [part]
  (let [warn (< (:collectionDayBattlesPlayed part) 3)
        bad (< (:battlesPlayed part) (:numberOfBattles part))
        rowclass (if bad "bad" (if warn "warn" "ok"))]
    [:tr {"class" rowclass} (map (fn [k] [:td (k part)]) war-participant-keys)]))

(defn render-war []
  (let [data (walk/keywordize-keys (api/my-clan-war))
        war-state (get-war-state data)
        tableId 0]
    (hu/html5
     (header "Current War")
     [:body
      (viewport)
      (topnav :war)
      [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
       [:table {"id" tableId}
        [:h1 "Current War"]
        [:h2 war-state]
        (war-participant-title tableId)
        (map war-participant (:participants data))]]])))

;; warlog (participant mapping is the same as the war)

(defn render-one-log [idx data]
  (let [war-season (:seasonId data)
        war-time (get-datetime (:createdDate data))
        tableId idx]
    (list
     [:h2 (str "Season " war-season " - War Creation Time: " war-time)]
     [:div {"style" "overflow-x:auto;"} ; horiz. scroll bar for table
      [:table {"id" tableId}
       (war-participant-title tableId)
       (map war-participant (:participants data))]])))

(defn render-warlog []
  (let [logs (:items (walk/keywordize-keys (api/my-clan-warlog)))]
    (hu/html5
     (header "War Log")
     [:body
      (viewport)
      (topnav :warlog)
      [:h1 "War Log"]
      (map-indexed render-one-log logs)])))

;; home

(defn render-home []
  (hu/html5
   (header "Home - CR Stats")
   [:body
    (viewport)
    (topnav :home)
    [:h1 "Home - CR Stats"]]))

;; not found

(defn render-not-found []
  (hu/html5
   (header "Page Not Found")
   [:body
    (viewport)
    [:h1 "Page Not Found"]]))
