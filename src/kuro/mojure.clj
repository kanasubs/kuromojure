(ns kuro.mojure
  (:use kuro.fude benri.kuro)
  (:require [clojure.string :as str]
            [clojure.set :refer [rename-keys]]
            [clojure.core.memoize :refer [memo]]
            [clojure.edn :as edn])
  (:import (org.atilika.kuromoji Tokenizer Tokenizer$Mode)))

(def ^:dynamic ^Tokenizer *tokenizer*)
(def ^:dynamic *mode*)
(def ^:dynamic *debug* nil)

(defmacro with-tokenizer
  "Builds a tokenizer with mode (:normal, :search, :extended) as input,
   providing a context in which the tokenizer can be used with fns that need it."
  [mode & body]
  `(let [wrapper-mode# (try (->> ~mode name str/upper-case)
                           (catch ClassCastException _# "NORMAL"))
         mode# (if (= wrapper-mode# "LEARNING") "SEARCH" wrapper-mode#)
         mode# (let [mode-str# "org.atilika.kuromoji.Tokenizer$Mode/"]
                 (->> mode# (str mode-str#) read-string eval))]
    (binding [*tokenizer* (-> (Tokenizer/builder) (.mode mode#) .build)
              *mode* wrapper-mode#]
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

(defn moji->clj-token
  "Accepts a org.atilika.kuromoji.Token and creates a Clojure map with the
   token attributes."
  [token]
  (-> token
      bean
      (update :allFeaturesArray seq)))

(defn ->exceptions
  "Accepts a clj-token and compounds the exceptions."
  [coll]
  (let [joined (str/join "-" coll)]
    (-> (fn [[old niw]]
          (let [replaced (str/replace-first joined (re-pattern old) niw)]
            (if-not (= joined replaced) replaced)))
        (keep-first [["動詞-非自立" "動詞,非自立"] ["形容詞-非自立" "形容詞,非自立"]
                     ["名詞-一般" "名詞,一般"]])
        (#(if % (str/split % #"-") coll)))))

(defn clj->en-token
  "Accepts a clj-token and translates its features to english."
  [token]
  (update-multi token
    {:partOfSpeech  (comp flatten (partial map jp->en) ->exceptions)
     :allFeaturesArray
       #(->> (drop-last 3 %) ->exceptions (map jp->en) flatten (<- (concat (take-last 3 %))))}))

(defn clj->mojure-token
  "Accepts a org.atilika.kuromoji.Token and then transforms its nature
   according to my opiniated perspective of what makes good Clojurian data."
  [token]
  (let [*->nil #(if-not (= % "*") %)]
    (-> token
        (update-multi {:partOfSpeech (comp #(map *->nil %) #(str/split % #","))
                       :allFeaturesArray (partial map *->nil)})
        clj->en-token
        (rename-keys {:baseForm :base-form
                      :partOfSpeech :part-of-speech
                      :surfaceForm :surface
                      :allFeaturesArray :features})
        (select-keys [:surface :known :reading :features :base-form :part-of-speech]))))

(defn raw-tokenize
  "Segments text into an ordered seq of org.atilika.kuromoji.Token tokens.
   Must be used in the context of with-tokenizer.
   Does not support :learning mode."
  [s] (seq (.tokenize *tokenizer* s)))

(defn- clj-tokenize-helper
  [s] (->> (raw-tokenize s) (map moji->clj-token)))

(defn ^String trim-unicode
  "Removes whitespace from both ends of string."
  {:added "1.2"}
  [^CharSequence s]
  (let [len (.length s)]
    (loop [rindex len]
      (if (zero? rindex)
        ""
        (if (or (Character/isWhitespace (.charAt s (dec rindex)))
                (= "　" (.charAt s (dec rindex))))
          (recur (dec rindex))
          ;; there is at least one non-whitespace char in the string,
          ;; so no need to check for lindex reaching len.
          (loop [lindex 0]
            (if (Character/isWhitespace (.charAt s lindex))
              (recur (inc lindex))
              (.. s (subSequence lindex rindex) toString))))))))

(defn clj-tokenize-with-spaces [text]
  (let [text-without-spaces (str/replace text #"[　 ]+" "")
        tokens (clj-tokenize-helper text-without-spaces)
        replace-fn (fn [s token-s]
                     (str/replace s (re-pattern (str "^" token-s)) ""))
        some-space-next #(some #{(-> % first str)} [" " "　"])
        next-space-fn #(some->> % some-space-next (hash-map :next-space))
        rm-leading-space #(apply str (drop 1 %))
        f (fn [[s coll] token]
              (let [replaced-text (replace-fn s (:surfaceForm token))
                    next-space (next-space-fn replaced-text)]
                [(if next-space (rm-leading-space replaced-text) replaced-text)
                 (conj coll (merge token next-space))]))]
            (second (reduce f [text []] tokens))))

(defn ->model
  [text & [token-fn]]
  (->> (clj-tokenize-with-spaces text)
       (map #(select-keys % [:surfaceForm :partOfSpeech :next-space]))
       (map #(merge (select-keys % [:next-space])
                    ((or token-fn identity) %)))
       (map #(into {} (remove (fn [[k v]] ((every-pred coll? empty?) v)) %)))
       (#(map (fn [item1 item2] [item1 item2]) % (rest %)))
       (map (partial apply hash-map))
       (apply merge-with (comp flatten list))
       (map-vals (comp frequencies flatten list))))

(defonce spaces (with-tokenizer (clj-tokenize-helper " 　"))) ; OPTIMIZE make this atom
(defonce half-space (first spaces))
(defonce full-space (second spaces))

(def model
  (with-tokenizer :learning (->model training-data)))

(defn max-key-next-tokens [next-tokens-with-frequency]
  (try
    (apply max-key last next-tokens-with-frequency)
  (catch Exception e
    (println "exception occurred")
    (throw (Exception. "bla")))))

(defn most-frequent-adjacent-pairs [tokens]
    (apply max-key (comp last last) tokens))

(defmulti add-space-info
  (fn [{:keys [surfaceForm]} _]
    (boolean (some #{surfaceForm} ["　" " " "\f"]))))

(defmethod add-space-info true [prev-token _]
  prev-token)

(defmethod add-space-info :default [prev-token next-token]
  (let [prev-token-candidates (find-token-candidates prev-token (keys model))
        prev-token-candidates-without-elect
          (mapv #(dissoc % :elect) prev-token-candidates)]
    (try
      (or
        (some->> (select-keys model prev-token-candidates-without-elect)
                 (->narrowed-model-for-nexts next-token)
                 (map (fn [[k v]] [k (max-key-next-tokens v)])) ; gets most frequent next for its prev
                 most-frequent-adjacent-pairs
                 first
                 ; FIXME all prev-token are same level, so any will do
                 (#(ffilter (fn [t] (= (dissoc t :elect) %)) prev-token-candidates))
                 (<- (select-keys [:elect :next-space])
                     (merge prev-token)))
        prev-token)
    (catch Exception e  ; FIXME this is not correct
      (assoc prev-token :exception e)))))

(defonce add-space-info-memo (memo add-space-info))

(defn add-debug-tokens [{:keys [exception elect]}]
  (filter :surfaceForm [{:surfaceForm exception} {:surfaceForm elect}]))

(defn add-space-token [debug token]
  (concat
    (case (:next-space token)
      nil [token] ; faster in the beginning (most common use case)
      " " [(dissoc token :next-space :elect :exception) half-space]
      "　" [(dissoc token :next-space :elect :exception) full-space])
    (if debug (add-debug-tokens token))))

(defonce add-space-token-memo (memo add-space-token))

(defn learning-tokenize [tokens]
  (->> (-> tokens vec peek)
       (conj (mapv add-space-info-memo tokens (rest tokens)))
       (mapcat (partial add-space-token-memo *debug*))))

(defn clj-tokenize
  "Segments text into an ordered seq of clj tokens.
   Must be used in the context of with-tokenizer."
  [s] (let [tokens (clj-tokenize-helper s)]
        (if (= *mode* "LEARNING")
          (learning-tokenize tokens)
          tokens)))

(defn tokenize
  "Segments text into an ordered seq of kuromojure tokens.
   Must be used in the context of with-tokenizer."
  [s] (map clj->mojure-token (clj-tokenize s)))