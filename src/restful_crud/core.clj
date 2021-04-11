(ns restful-crud.core
  (:require [toucan.db :as db]
            [toucan.models :as models]
            [ring.adapter.jetty :refer [run-jetty]]
            [compojure.api.sweet :refer [api routes]]
            [restful-crud.user :refer [user-routes]]
            [restful-crud.book :refer [book-routes]]
            [restful-crud.user :refer [user-entity-route]]
            [restful-crud.book :refer [book-entity-route]])

  (:gen-class))

(def db-spec
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "database.db"})

(def swagger-config
  {:ui "/swagger"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}})


;(def app (api {:swagger swagger-config} (apply routes (concat user-routes book-routes))))
(def app (api {:swagger swagger-config}
           (apply routes book-entity-route user-entity-route)))

(defn -main
  [& args]
  (db/set-default-db-connection! db-spec)
  (models/set-root-namespace! 'restful-crud.models)
  (run-jetty app {:port 3000}))

; https://www.demystifyfp.com/clojure/blog/restful-crud-apis-using-compojure-api-and-toucan-part-1/
; https://www.demystifyfp.com/clojure/blog/restful-crud-apis-using-compojure-api-and-toucan-part-2/

