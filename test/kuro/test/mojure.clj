(ns kuro.test.mojure
  (:use midje.sweet kuro.mojure kuro.fude))

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
  (with-tokenizer
    (-> "日本" tokenize first)) =>
      {:surface "日本"
       :known true
       :reading "ニッポン"
       :features [:noun :proper :place :country nil nil "日本" "ニッポン" "ニッポン"]
       :base-form "日本"
       :part-of-speech [:noun :proper :place :country]})

(fact "clj-tokenize for translating to Japanese or repurposing the library."
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
         :unknown false}

      (add-space-info nihon-token particle-token)
        => (contains {:next-space " " :elect 1})

    (->model "日本 に　行きます。" clj->mojure-token) =>
      {{:surface "に" :part-of-speech [:particle :case :misc nil] :next-space "　" :prev-space " "}
         {{:surface "行き" :part-of-speech [:verb :main nil nil] :prev-space "　"} 1}
       {:surface "行き" :part-of-speech [:verb :main nil nil] :prev-space "　"}
         {{:surface "ます" :part-of-speech [:auxiliary-verb nil nil nil]} 1}
       {:surface "ます" :part-of-speech [:auxiliary-verb nil nil nil]}
         {{:surface "。" :part-of-speech [:symbol :period nil nil]} 1}
       {:surface "日本" :part-of-speech [:noun :proper :place :country] :next-space " "}
         {{:surface "に" :part-of-speech [:particle :case :misc nil] :next-space "　" :prev-space " "} 1}}

    (let [sample-model (->model "日本 に　行きます。")]

      sample-model =>
        {{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}
           {{:prev-space " " :next-space "　" :partOfSpeech "助詞,格助詞,一般,*" :surfaceForm "に"} 1}
         {:prev-space " " :next-space "　" :partOfSpeech "助詞,格助詞,一般,*" :surfaceForm "に"}
           {{:prev-space "　" :partOfSpeech "動詞,自立,*,*" :surfaceForm "行き"} 1}
         {:prev-space "　" :partOfSpeech "動詞,自立,*,*" :surfaceForm "行き"}
           {{:partOfSpeech "助動詞,*,*,*" :surfaceForm "ます"} 1}
         {:partOfSpeech "助動詞,*,*,*" :surfaceForm "ます"}
           {{:partOfSpeech "記号,句点,*,*" :surfaceForm "。"} 1}}

      (elect-by :surface-and-part-of-speech 4 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :surface-and-part-of-speech 3 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :surface-and-part-of-speech 2 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :surface-and-part-of-speech 1 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :part-of-speech 4 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :part-of-speech 3 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :part-of-speech 2 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :part-of-speech 1 nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (elect-by :surface nihon-token (keys sample-model)) =>
        [{:next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]
      (find-token-candidates nihon-token (keys sample-model)) =>
        [{:elect 1 :next-space " " :partOfSpeech "名詞,固有名詞,地域,国" :surfaceForm "日本"}]))))

(fact
  (->exceptions ["形容詞" "非自立"]) => ["形容詞,非自立"])

(fact
  (let [sentence "日本に行きます。"
        tokens (with-tokenizer :learning (clj-tokenize sentence))
        tokens-with-debug
          (binding [*debug* true]
            (with-tokenizer :learning (clj-tokenize sentence)))]
    (map :surfaceForm tokens) => ["日本" " " "に" "　" "行き" "ます" "。"]
    (map :surfaceForm tokens-with-debug) => ["日本" " " 1 "に" "　" 1 "行き" 1 "ます" 1 "。"]))

(fact
  (->> (with-tokenizer :learning (tokenize "日本に行きます。")) (map :surface))
    => ["日本" " " "に" "　" "行き" "ます" "。"])

(fact
  (->> (binding [*debug* true]
         (with-tokenizer :learning
           (clj-tokenize-with-spaces "でもクーラーの温度の下げすぎは体によくありません。")))
       (map :surfaceForm))
    => nil)