(defproject clash "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 ;;[http-kit "2.3.0"]
                 [http-kit "2.4.0-alpha3"]
                 [ring/ring-codec "1.1.2"]
                 [compojure "1.6.1"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.7"]
                 [clojure.java-time "0.3.2"]]
  ;;:main ^:skip-aot clash.core
  :uberjar-name "clash-standalone.jar"
  ;;:profiles {:uberjar {:aot :all}}
  :profiles {:production {:env {:production true}}}
  )
