(ns role-assigner-bot.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.core.async :as ch :refer [chan close!]]
            [clojure.java.io :as io]
            [discljord.messaging :as discord-rest]
            [discljord.connections :as discord-ws]
            [discljord.formatting :refer [mention-user]]
            [discljord.events :refer [message-pump!]]))

(def state (atom nil))

(def bot-id (atom nil))

(def config (edn/read-string (slurp (io/resource "config.edn"))))

(defn random-response [user]
  (str (rand-nth (:responses config)) ", " (mention-user user) \!))

(defmulti handle-event (fn [type _data] type))

(defmethod handle-event :message-reaction-add
  [_ {:keys [user-id message-id emoji guild-id]}]
  (let [emoji-name (:name emoji)]
    (when (and (= message-id (:roles-message-id config))
               (contains? (:emojis->roles config) emoji-name))
      (discord-rest/add-guild-member-role! (:rest @state) guild-id user-id (get (:emojis->roles config) emoji-name)))))

(defmethod handle-event :message-reaction-remove
  [_ {:keys [user-id message-id emoji guild-id]}]
  (let [emoji-name (:name emoji)]
    (when (and (= message-id (:roles-message-id config))
               (contains? (:emojis->roles config) emoji-name))
      (discord-rest/remove-guild-member-role! (:rest @state) guild-id user-id (get (:emojis->roles config) emoji-name)))))

(defmethod handle-event :message-create
  [_ {:keys [channel-id author mentions]}]
  (when (some #{@bot-id} (map :id mentions))
    (discord-rest/create-message! (:rest @state) channel-id :content (random-response author))))

(defmethod handle-event :default [_ _])

(defn start-bot! [token & intents]
  (let [event-channel (chan 100)
        gateway-connection (discord-ws/connect-bot! token event-channel :intents (set intents))
        rest-connection (discord-rest/start-connection! token)]
    {:events  event-channel
     :gateway gateway-connection
     :rest    rest-connection}))

(defn stop-bot! [{:keys [rest gateway events] :as _state}]
  (discord-rest/stop-connection! rest)
  (discord-ws/disconnect-bot! gateway)
  (close! events))

(defn -main [& args]
  (reset! state (start-bot! (:token config) :guild-messages :guild-message-reactions))
  (reset! bot-id (:id @(discord-rest/get-current-user! (:rest @state))))
  (try
    (message-pump! (:events @state) handle-event)
    (finally (stop-bot! @state))))

(comment
  (defonce bot (ch/go (-main))))
