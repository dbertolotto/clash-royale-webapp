(ns clash.api
  (:gen-class)
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.data.codec.base64 :as b64]
            [clojure.string :as cljstr]
            [ring.util.codec :as rcod]
            [environ.core :refer [env]]))

;; utils

(defn str-to-b64-str [original]
  (String. (b64/encode (.getBytes original)) "UTF-8"))

;; defs

;; hardcode player and clan
(def my-clan "#G2RYVP")
(def my-player "#RUGCQJ9")

;; api base url
(def server "https://api.clashroyale.com")
(def version "v1")

;; token
(def token (env :api-token))
(def options {:headers {"Accept" "application/json"
                        "authorization" (str "Bearer " token)}})

;; proxy
(def proxy-url (env :fixie-url))
(def proxy-url-match (if proxy-url
                       (re-matches #"(http[s]?://)(.*?):(.*?)@(.*?):(\d+)" proxy-url)))
(def proxy-map (if proxy-url-match
                 {:auth (str-to-b64-str (str (proxy-url-match 2) ":" (proxy-url-match 3)))
                  :host (str (proxy-url-match 1) (proxy-url-match 4))
                  :port (Integer/parseInt (proxy-url-match 5))}))
(def proxy-options (if proxy-map
                     {:headers (assoc (:headers options)
                                      "Proxy-Authorization"
                                      (str "Basic " (:auth proxy-map)))
                      :proxy-host (:host proxy-map)
                      :proxy-port (:port proxy-map)}))

;; core functions

(defn api-url [& args]
  (cljstr/join "/" (flatten (list server version args))))

(defn api-opt [[k v]]
  (str k "=" v))

(defn api-opts [m]
  (if m
    (str "?" (cljstr/join "&" (map api-opt (seq m))))))

(defn api-get [url]
  (let [resp @(http/get url (or proxy-options options))]
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
