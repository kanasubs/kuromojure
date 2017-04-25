(defproject kuromojure "0.7.0"

  :url "https://github.com/ccfontes/kuromojure"

  :description "Clojure wrapper for kuromoji"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :scm {:name "git"
        :url "https://github.com/ccfontes/kuromojure"}

  :repositories
    [["kuromoji" "http://www.atilika.org/nexus/content/repositories/atilika"]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]]

  :profiles {:dev {:plugins [[lein-midje "3.2.1"] [codox "0.8.9"]]
                   :dependencies [[midje "1.8.3"]]}}

  :repl-options {:init-ns kuro.mojure}

  :codox {:src-dir-uri "https://github.com/ccfontes/kuromojure/blob/master/"
          :src-linenum-anchor-prefix "L"})
