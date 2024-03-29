(defproject clash "0.1.0-SNAPSHOT"
  :description "Clash Royale Stats web app"
  :url "https://my-cr-webapp.herokuapp.com/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [http-kit "2.4.0-alpha3"] ; need to use the alpha version because of a bug
                 [ring/ring-codec "1.1.2"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.7"]
                 [clojure.java-time "0.3.2"]
                 [org.clojure/data.codec "0.1.1"]
                 [environ "1.1.0"]]
  :min-lein-version "2.0.0" ; to use lein >= 2.0 on heroku
  :main ^:skip-aot clash.core
  :uberjar-name "clash-standalone.jar"
  :profiles {:production {:env {:production true}}
             :uberjar {:aot :all}})
