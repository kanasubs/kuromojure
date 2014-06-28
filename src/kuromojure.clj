(ns kuromojure
  (:use benrikuro)
  (:require [clojure.string :refer [join split upper-case replace-first]]
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
  {; TODO organize into categories:
   ;   - adjectives
   ;   - adverbs‎
   ;   - conjunctions‎
   ;   - counters‎
   ;   - interjections‎
   ;   - interrogatives‎
   ;   - nouns‎
   ;   - numerals‎
   ;   - particles‎
   ;   - postpositions‎
   ;   - pronouns‎
   ;   - verbs‎
   "固有名詞" :proper, "特殊" :special, "一般" :misc
   "名詞" :noun, "代名詞" :pronoun
   "名" :given-name, "姓" :surname, "人名" :person
   "組織" :organization "地域" :place "国" :country
   "助詞" :particle "助詞類接続" :particle_conjunction "接続詞" :conjunction
   "縮約" :contraction "感動詞" :interjection "間投" :interjection
   "引用文字列" :quotation "引用" :quote "連語" :compound
    "接頭詞" :prefix "接尾" :suffix "接続詞的" :suffix-conjunctive
   "助数詞" :classifier
   "助動詞語幹" :aux "助動詞" :auxiliary-verb "非自立" :affix
   "動詞" :verb "副詞" :adverb
   "サ変接続" :verbal "動詞接続" :verbal "非言語音" :non-verbal
   "副詞可能" :adverbial "副助詞" :adverbial "名詞接続" :nominal
   "連体詞" :adnominal "連体化" :adnominalizer "副詞化" :adnominalizer
   "アルファベット" :alphabetic "接続助詞" :conjunctive
   "形容詞" :adjective "ナイ形容詞語幹" :nai-adjective "形容動詞語幹" :adjective-base
   "数" :numeric "数接続" :numerical "形容詞接続" :adjectival "終助詞" :final
   "間投助詞" :interjective "並立助詞" :coordinate "動詞非自立的" :verbal_aux
   "自立" :main "基本形" :basic-form
   "記号" :symbol "句点" :period "空白" :space "読点" :comma
   "括弧開" :open-bracket "括弧閉" :close-bracket
   "格助詞" :case "係助詞" :dependency
   "副助詞／並立助詞／終助詞" :adverbial/conjunctive/final
   "その他" :other "フィラー" :filler "語断片" :fragment "未知語" :unknown
   ; exceptions
   "動詞,非自立" [:verb :auxiliary] "形容詞,非自立" [:adjective :auxiliary]
   "名詞,一般" [:noun :common]})

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

(defn keep-first
  "Returns the first (pred item) for which value is not nil.
   Consumes sequences up to the first match, will consume the entire sequence
   and return nil if no match is found."
  [pred coll] (一 (keep pred coll)))

(defn ->exceptions
  "Accepts a clj-token and compounds the exceptions."
  [coll]
  (let [joined (join "-" coll)]
    (-> (fn [[old niw]]
          (let [replaced (replace-first joined (re-pattern old) niw)]
            (if-not (= joined replaced) replaced)))
        (keep-first [["動詞-非自立" "動詞,非自立"] ["形容詞-非自立" "形容詞,非自立"]
                     ["名詞-一般" "名詞,一般"]])
        (#(if % (split % #"-") coll)))))

(defn ->en-token
  "Accepts a clj-token and translates its features to english."
  [token]
  (update-multi token
    {:partOfSpeech  (comp flatten (partial map jp->en) ->exceptions)
     :allFeaturesArray
       #(->> (drop-last 3 %) ->exceptions (map jp->en) flatten (<- (concat (take-last 3 %))))}))

(defn ->kuromojure-token
  "Accepts a org.atilika.kuromoji.Token and then transforms its nature
   according to my opiniated perspective of what makes good Clojurian data."
  [token]
  (-> token
      ->clj-token
      ->en-token
      (update :allFeaturesArray (fn [coll] (map #(if-not (= % "*") %) coll)))
      (rename-keys {:baseForm :base-form
                    :partOfSpeech :part-of-speech
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