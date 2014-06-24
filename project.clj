(defproject kuromojure "0.3.0"
  :source-paths [""]
  :url "https://github.com/ccfontes/kuromojure"
  :license {:name "Eclipse Public License" :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories
    [["kuromoji" "http://www.atilika.org/nexus/content/repositories/atilika"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.atilika.kuromoji/kuromoji "0.7.7"]
                 [camel-snake-kebab "0.1.5"]
                 [benrikuro "0.2.2-SNAPSHOT"]]
  :repl-options {:init-ns kuromojure})