(ns kuromojure
  (:use benrikuro)
  (:require [clojure.string :refer [split upper-case]]
  	        [clojure.set :refer [rename-keys]])
  (:import (org.atilika.kuromoji Tokenizer Tokenizer$Mode)))

(def ^:dynamic ^Tokenizer *tokenizer*)

(defmacro with-tokenizer
  "Builds a tokenizer with mode (:normal, :search, :extended) as input,
   providing a context in which the tokenizer can be used with fns that need it."
  [mode & body]
  `(let [mode#
           (try
             (let [mode-str# "org.atilika.kuromoji.Tokenizer$Mode/"]
               (->> ~mode name upper-case (str mode-str#) read-string eval))
             (catch ClassCastException _# Tokenizer$Mode/NORMAL))]
    (binding [*tokenizer* (-> (Tokenizer/builder) (.mode mode#) .build)]
      ~mode ~@body)))

(defn- clojurify-token
  "Accepts a org.atilika.kuromoji.Token and then hash maps, removes,
   renames and transforms its properties according to a Clojurian perspective."
  [token]
  (-> token
  	  bean
  	  (rename-keys {:baseForm :base
                    :partOfSpeech :classes
                    :surfaceForm :surface
                    :allFeaturesArray :features})
  	  (dissoc :class :unknown :user :allFeatures :position)
  	  (update-multi {:features seq :classes #(split % #",")})))

(defn tokenize
  "Segments text into an ordered seq of clojurified token maps.
   Must be used in the context of with-tokenizer."
  [s] (->> (.tokenize *tokenizer* s) seq (map clojurify-token)))