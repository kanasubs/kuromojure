(defproject kuromojure "0.4.0"
  
  :source-paths ["src"]
  :test-paths ["test"]

  :url "https://github.com/ccfontes/kuromojure"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories
    [["kuromoji" "http://www.atilika.org/nexus/content/repositories/atilika"]]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]
                 [benrikuro "0.2.3"]]

  

  :profiles {:dev {:plugins [[lein-midje "3.1.3"]]
                   :dependencies [[midje "1.6.3"]]}}

  :repl-options {:init-ns kuromojure})