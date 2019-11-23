(ns clash.core
  (:gen-class)
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [clash.render :as render]
            [environ.core :refer [env]]))


(defn handler [status header-type render-fun]
  {:status  status
   :headers {"Content-Type" (str (case header-type
                                   :html "text/html"
                                   :json "text/json")
                                 "; charset=utf-8")}
   :body    (render-fun)})

(defn home-handler [req]
  (handler 200 :html render/render-home))

(defn members-handler [req]
  (handler 200 :html render/render-members))

(defn war-handler [req]
  (handler 200 :html render/render-war))

(defn warlog-handler [req]
  (handler 200 :html render/render-warlog))

(defn members-json-handler [req]
  (handler 200 :json render/render-members-json))

(defn war-json-handler [req]
  (handler 200 :json render/render-war-json))

(defn warlog-json-handler [req]
  (handler 200 :json render/render-warlog-json))

(defn not-found-handler [req]
  (handler 404 :html render/render-not-found))

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
