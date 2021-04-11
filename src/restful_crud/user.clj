(ns restful-crud.user
  (:use clojure.pprint)
  (:require [schema.core :as s]
            [restful-crud.string-util :as str]
            [buddy.hashers :as hashers]
            [clojure.set :refer [rename-keys]]
            [ring.util.http-response :refer [created]]
            [toucan.db :as db]
            [restful-crud.models.user :refer [User]]
            [compojure.api.sweet :refer [POST GET PUT DELETE]]
            [ring.util.http-response :refer [created ok not-found]]
            [restful-crud.restful :as restful]))

(defn valid-username?
  [name]
  (str/non-blank-with-max-length? 50 name))

(defn valid-password?
  [password]
  (str/length-in-range? 5 50 password))

(s/defschema  UserRequestSchema
  {:username (s/constrained s/Str valid-username?)
   :password (s/constrained s/Str valid-password?)
   :email    (s/constrained s/Str str/email?)})

(defn id->created
  [id]
  (created (str "/users/" id) {:id id}))

(defn canonicalize-user-req
  [user-req]
  (-> (update user-req :password hashers/derive)
      (rename-keys {:password :password_hash})))

(defn create-user-handler
  [create-user-req]
  (->> (canonicalize-user-req create-user-req)
       (db/insert! User)
       :id
       id->created))

(defn update-user-handler
  [id update-user-req]
  (db/update! User id (canonicalize-user-req update-user-req))
  (ok))

(defn delete-user-handler
  [user-id]
  (db/delete! User :id user-id)
  (ok))

(defn delete-users-handler
  []
  (db/delete! User)
  (ok))

(defn user->response
  [user]
  (if user
    (ok user)
    (not-found "not-found")))

(defn get-users-handler
  []
  (->> (db/select User)
       (map #(dissoc % :password_hash))
       ok))

(defn get-user-handler
  [user-id]
  ( -> (User user-id)
       (dissoc :password_hash)
       user->response))

(def user-routes
  [(POST "/users" []
    :body [create-user-req UserRequestSchema]
    (create-user-handler create-user-req))

   (GET "/users/:id" []
     :path-params [id :- s/Int]
     (get-user-handler id))

   (GET "/users" []
     (get-users-handler))

   (PUT "/users/:id" []
     :path-params [id :- s/Int]
     :body [update-user-req UserRequestSchema]
     (update-user-handler id update-user-req))

   (DELETE "/users/:id" []
     :path-params [id :- s/Int]
     (delete-user-handler id))

   (DELETE "/users" []
     (delete-users-handler))])

(def user-entity-route
  (restful/resource {:model User
                     :name "users"
                     :req-schema UserRequestSchema
                     :leave #(dissoc % :password_hash)
                     :enter canonicalize-user-req}))
