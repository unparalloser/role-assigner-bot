(defproject role-assigner-bot "0.1.0-SNAPSHOT"
  :description "A Discord bot that assigns roles based on the reactions to a particular message."
  :license {:name "CC0-1.0"
            :url "https://creativecommons.org/publicdomain/zero/1.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.suskalo/discljord "1.1.1"]]
  :repl-options {:init-ns role-assigner-bot.core}
  :main role-assigner-bot.core)
