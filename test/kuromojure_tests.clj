(ns kuromojure-tests
  (:use midje.sweet kuromojure))

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

(fact "Implicit mode defaults to :normal. Map properties are delicious."
  (with-tokenizer (->> "日本" tokenize first))
    => {:surface "日本"
        :known true
        :reading "ニッポン"
        :features ["noun" "proper-noun" "region" "state" "*" "*" "日本" "ニッポン" "ニッポン"]
        :base "日本"
        :classes ["noun" "proper-noun" "region" "state"]})