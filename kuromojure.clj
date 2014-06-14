(ns kuromojure
  (:import [org.atilika.kuromoji Tokenizer Tokenizer$Mode]))

(def ^:dynamic ^Tokenizer *tokenizer*)

(defn get-surface-form [token]
  (.getSurfaceForm token))

(defn get-reading [token]
  (.getReading token))

(defn get-part-of-speech [token]
  (.getPartOfSpeech token))

(defmacro with-tokenizer
  [& body] ; (.build (Tokenizer/builder))
  `(let [mode# (Tokenizer$Mode/SEARCH)]
    (binding [*tokenizer* (-> (Tokenizer/builder) (.mode mode#) .build)]
      ~@body)))

(defn tokenize
  [s] (seq (.tokenize *tokenizer* s)))