(defproject restful-crud "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]

                 ; web
                 [prismatic/schema "1.1.9"]
                 [metosin/compojure-api "2.0.0-alpha26"
                  :exclusions [frankiesardo/linked]]
                 [ikitommi/linked "1.3.1-alpha1"]
                 [ring/ring-jetty-adapter "1.7.0"]

                 ; database
                 [toucan "1.1.9"]
                 [org.xerial/sqlite-jdbc "3.34.0"]

                 ; password hasking
                 [buddy/buddy-hashers "1.3.0"]]
  :main ^:skip-aot restful-crud.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
