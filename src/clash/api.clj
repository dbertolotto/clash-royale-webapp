(ns clash.api
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.string :as cljstr]
            [ring.util.codec :as rcod]))

(def my-clan "#G2RYVP")
(def my-player "#RUGCQJ9")

(def server "https://api.clashroyale.com")
(def version "v1")
(def token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImI0YThlZmZjLWRlOGUtNGRiNS1iMDc4LTgyMjVmNTE2ZDRkNCIsImlhdCI6MTU3NDQxMDc4Mywic3ViIjoiZGV2ZWxvcGVyL2E5ZDAxMWI3LWFmMDYtNzU3ZS04OWY5LTJjMjQ1OTZlMjQxNyIsInNjb3BlcyI6WyJyb3lhbGUiXSwibGltaXRzIjpbeyJ0aWVyIjoiZGV2ZWxvcGVyL3NpbHZlciIsInR5cGUiOiJ0aHJvdHRsaW5nIn0seyJjaWRycyI6WyIxNzguMTk3LjIzOS4xMzQiXSwidHlwZSI6ImNsaWVudCJ9XX0.LF5metIrb7HaRLwG6hLt97vy2Gpu8PSjw2HNwhNGQtsDy0B1zzjXkCMpN2jXdYSPd1VQszwvNJLrFerzYt0YuQ")


;; core functions

(defn api-url [& args]
  (cljstr/join "/" (flatten (list server version args))))

(defn api-opt [[k v]]
  (str k "=" v))

(defn api-opts [m]
  (if m
    (str "?" (cljstr/join "&" (map api-opt (seq m))))))

(defn api-get [url]
  (let [options {:headers {"Accept" "application/json"
                           "authorization" (str "Bearer " token)}}
        resp @(http/get url options)]
    (json/read-str (:body resp))))

;; internal api

(defn cards []
  (api-get (api-url "cards")))

(defn player [tag]
  (api-get (api-url "players" (rcod/url-encode tag))))

(defn clan [tag]
  (api-get (api-url "clans" (rcod/url-encode tag))))

(defn clan-members [tag]
  (api-get (api-url "clans" (rcod/url-encode tag) "members")))

(defn clan-war [tag]
  (api-get (api-url "clans" (rcod/url-encode tag) "currentwar")))

(defn clan-warlog [tag n]
  (api-get (api-url "clans" (rcod/url-encode tag) "warlog" (api-opts {"limit" n}))))

;; helpers

(defn my-clan-members []
  (clan-members my-clan))
(defn my-clan-war []
  (clan-war my-clan))
(defn my-clan-warlog []
  (clan-warlog my-clan 5))
