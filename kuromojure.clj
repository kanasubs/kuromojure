(ns kuromojure
  (:use benrikuro)
  (:require [clojure.string :refer [upper-case]]
  	        [camel-snake-kebab :refer [->kebab-case]])
  (:import [org.atilika.kuromoji Tokenizer Tokenizer$Mode]))

(def ^:dynamic ^Tokenizer *tokenizer*)

(defmacro with-tokenizer
  [mode & body]
  `(try
    (let [mode# (->> ~mode name upper-case (str "Tokenizer$Mode/") read-string eval)]
      (binding [*tokenizer* (-> (Tokenizer/builder) (.mode mode#) .build)]
        ~@body))
    (catch ClassCastException _#
      (binding [*tokenizer* (-> (Tokenizer/builder) (.mode (Tokenizer$Mode/NORMAL)) .build)]
        ~mode ~@body))))

(defn tokenize
  "Segments text into a seq of token maps. Must be used inside with-tokenizer."
  [s]
  (let [f (comp (partial kmap ->kebab-case) bean)
  	    seq-all-features-array #(update % :all-features-array seq)]
    (->> (.tokenize *tokenizer* s) seq (map f) (map seq-all-features-array))))