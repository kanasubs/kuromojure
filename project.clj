(defproject kuromojure "0.6.0"

  :url "https://github.com/ccfontes/kuromojure"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories
    [["kuromoji" "http://www.atilika.org/nexus/content/repositories/atilika"]]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]
                 [benrikuro "0.2.4"]]

  :profiles {:dev {:plugins [[lein-midje "3.1.3"] [codox "0.8.9"]]
                   :dependencies [[midje "1.6.3"]]}}

  :repl-options {:init-ns kuromojure}

  :codox {:src-dir-uri "https://github.com/ccfontes/kuromojure/blob/master/"
          :src-linenum-anchor-prefix "L"})