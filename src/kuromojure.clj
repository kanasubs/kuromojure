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

(defonce jp->en-mapping
  {"名詞"    "noun"
   "固有名詞" "proper-noun"
   "地域"    "region"
   "国"      "state"
   "一般"    "common"
   "助詞"    "particle"
   "助動詞"  "aux-verb"
   "特殊"    "special"
   "基本形"  "basic-form"
   "記号"    "symbol"
   "句点"    "full-stop"})

(defn jp->en
  "Converts word in Japanese to English. Defaults to English in case it's not
   mapped yet."
  [word] (or-> word jp->en-mapping))

(defn ->clj-token
  "Accepts a org.atilika.kuromoji.Token and creates a Clojure map with the
   token attributes."
  [token]
  (-> token
      bean
      (update-multi {:allFeaturesArray seq :partOfSpeech #(split % #",")})))

(defn ->en-token
  "Accepts a base-clj token and translates its features to english."
  [token]
  (update-each token [:partOfSpeech :allFeaturesArray] (partial map jp->en)))

(defn ->kuromojure-token
  "Accepts a org.atilika.kuromoji.Token and then transforms its nature
   according to my opiniated perspective of what makes good Clojurian data."
  [token]
  (-> token
      ->clj-token
      ->en-token
      (rename-keys {:baseForm :base
                    :partOfSpeech :classes
                    :surfaceForm :surface
                    :allFeaturesArray :features})
      (dissoc :class :unknown :user :allFeatures :position)))

(defn raw-tokenize
  "Segments text into an ordered seq of org.atilika.kuromoji.Token tokens.
   Must be used in the context of with-tokenizer."
  [s] (seq (.tokenize *tokenizer* s)))

(defn clj-tokenize
  "Segments text into an ordered seq of clj tokens.
   Must be used in the context of with-tokenizer."
  [s] (map ->clj-token (raw-tokenize s)))

(defn tokenize
  "Segments text into an ordered seq of kuromojure tokens.
   Must be used in the context of with-tokenizer."
  [s] (map ->kuromojure-token (raw-tokenize s)))