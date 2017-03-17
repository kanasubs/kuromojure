(defproject ccfontes/kuromojure "0.6.2"

  :url "https://github.com/ccfontes/kuromojure"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

;  :repositories
;    [["kuromoji" "http://www.atilika.org/nexus/content/repositories/atilika"]]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.match "0.2.1"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]
                 [benrikuro "0.6.0"]

  :profiles {:dev {:plugins [[lein-midje "3.1.3"] [codox "0.8.9"]]
                   :dependencies [[midje "1.6.3"]]}}

  :repl-options {:init-ns kuro.mojure}

  :codox {:src-dir-uri "https://github.com/ccfontes/kuromojure/blob/master/"
          :src-linenum-anchor-prefix "L"})
