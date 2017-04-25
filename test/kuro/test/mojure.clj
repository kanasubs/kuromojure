(ns kuro.test.mojure
  (:use midje.sweet kuro.mojure))

(fact "Kuromoji supports segmentation modes that gives different segmentations based on the application in mind:
       Normal - regular segmentation
       Search - use a heuristic to do additional segmentation useful for search
       Extended - similar to search mode, but also unigram unknown words (experimental)
       The below table gives some examples of these modes."
  (with-tokenizer :normal
    (->> "関西国際空港" tokenize (map :surface)) => ["関西国際空港"]
    (->> "日本経済新聞" tokenize (map :surface)) => ["日本経済新聞"]
    (->> "シニアソフトウェアエンジニア" tokenize (map :surface)) => ["シニアソフトウェアエンジニア"]
    (->> "ディジカメを買った" tokenize (map :surface)) => ["ディジカメ" "を" "買っ" "た"])

  (with-tokenizer :search
    (->> "関西国際空港" tokenize (map :surface)) => ["関西" "国際" "空港"]
    (->> "日本経済新聞" tokenize (map :surface)) => ["日本" "経済" "新聞"]
    (->>  "シニアソフトウェアエンジニア" tokenize (map :surface)) => ["シニア" "ソフトウェア" "エンジニア"]
    (->>  "ディジカメを買った" tokenize (map :surface)) => ["ディジカメ" "を" "買っ" "た"])

  (with-tokenizer :extended
    (->> "関西国際空港" tokenize (map :surface)) => ["関西" "国際" "空港"]
    (->> "日本経済新聞" tokenize (map :surface)) => ["日本" "経済" "新聞"]
    (->> "シニアソフトウェアエンジニア" tokenize (map :surface)) => ["シニア" "ソフトウェア" "エンジニア"]
    (->> "ディジカメを買った" tokenize (map :surface)) => ["デ" "ィ" "ジ" "カ" "メ" "を" "買っ" "た"]))

(fact "Implicit mode defaults to ':normal'."
  (with-tokenizer
    (-> "日本" tokenize first)) =>
      {:surface "日本"
       :known true
       :reading "ニッポン"
       :features [:noun :proper :place :country nil nil "日本" "ニッポン" "ニッポン"]
       :base-form "日本"
       :part-of-speech [:noun :proper :place :country]})

(fact "clj-tokenize for repurposing the library."
  (with-tokenizer

    (let [[nihon-token particle-token] (clj-tokenize "日本に")]
      nihon-token =>
        {:surfaceForm "日本"
         :known true
         :reading "ニッポン"
         :allFeatures "名詞,固有名詞,地域,国,*,*,日本,ニッポン,ニッポン"
         :baseForm "日本"
         :partOfSpeech "名詞,固有名詞,地域,国"
         :position 0
         :allFeaturesArray ["名詞" "固有名詞" "地域" "国" "*" "*" "日本" "ニッポン" "ニッポン"]
         :class org.atilika.kuromoji.Token
         :user false
         :unknown false})))

(fact
  (->exceptions ["形容詞" "非自立"]) => ["形容詞,非自立"])
