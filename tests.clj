(ns tests
  (:use midje.sweet kuromojure))

(fact
  (with-tokenizer
    (-> "私は犯人ですよ。" tokenize second .getPartOfSpeech) => "助詞,係助詞,*,*"
    (-> "早速見てみようぜ。" tokenize (nth 4) .getPartOfSpeech) => "助動詞,*,*,*"
    (-> "カルロスさんが駅へ行く。" tokenize (nth 1) .getPartOfSpeech) => "名詞,接尾,人名,*"
    (-> "！" tokenize first .getPartOfSpeech) => "記号,一般,*,*"
    (-> "速い" tokenize first get-surface-form) => "速い"
    (-> "速い" tokenize first get-reading) => "ハヤイ"
    (-> "シニアソフトウェアエンジニア" tokenize first get-surface-form) => "シニア"
    (-> "食べる" tokenize first get-surface-form) => "食べる"))
    ;(reading (tokenize "速")) => "ハヤイ"))
    ;(reading "Clojureは速い。") => ["Clojure"　"ハ"　"ハヤイ"　"。"]
    ;(reading "多摩川のたまちゃん") => ["タマガワ"　"ノ"　"タマ"　"チャン"]
    ;(reading "写真とビデオのインポート") => ["シャシン"　"ト"　"ビデオ"　"ノ"　"イン"　"ポート"]
    ;(reading "南太平洋のブロードウェイミュージカル") => ["ミナミタイヘイヨウ"　"ノ"　"ブロードウェイ"　"ミュージカル"]))