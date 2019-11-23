(ns clash.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clash.render :as render]
            [environ.core :refer [env]]))


(defn home-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render/render-home)})

(defn members-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render/render-members)})

(defn war-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render/render-war)})

(defn warlog-handler [req]
  {:status  200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render/render-warlog)})

(defn members-json-handler [req]
  {:status  200
   :headers {"Content-Type" "text/json; charset=utf-8"}
   :body    (render/render-members-json)})

(defn war-json-handler [req]
  {:status  200
   :headers {"Content-Type" "text/json; charset=utf-8"}
   :body    (render/render-war-json)})

(defn warlog-json-handler [req]
  {:status  200
   :headers {"Content-Type" "text/json; charset=utf-8"}
   :body    (render/render-warlog-json)})

(defn not-found-handler [req]
  {:status  404
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body    (render/render-not-found)})

(defroutes app-routes
  (GET "/" [] home-handler)
  (GET "/members" [] members-handler)
  (GET "/war" [] war-handler)
  (GET "/warlog" [] warlog-handler)
  (GET "/members/json" [] members-json-handler)
  (GET "/war/json" [] war-json-handler)
  (GET "/warlog/json" [] warlog-json-handler)
  ;; special route for serving static files like css
  ;; default root directory is resources/public/
  (route/resources "/")
  (route/not-found not-found-handler))

(defn -main
  "This is our app's entry point"
  [& args]
  (let [port (Integer/parseInt (or (env :port) "8080"))]
  (server/run-server #'app-routes {:port port})
  (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
