(ns kuro.fude
  (:use benri.kuro)
  (:require [clojure.string :as str]
            [clojure.core.match :refer [match]]))
; (->> (with-tokenizer :learning (clj-tokenize "こんなダルい世界は　もう飽きた")) (map :surfaceForm))
; FIXME non-idiomatic yojijukugo should go here. They NEVER come with a space
(def training-data
  (str "後に　就いて　言って　下さい。 もう一度。 全部　一緒 に。 はじめ から。"
       "次 を　どうぞ。 よく　できる。 おはよう　ございます。 どうも　ありがとう　ございます。 "
       "どういたしまして。 お元気　です　か。 いかが　です　か。 元気　です。 まあまあ　です。 "
       "おかげさまで。 お蔭様 で。 行って　来ます。 行って　いらっしゃい。 行ってらっしゃい。 "
       "ただいま。 お帰りなさい。 Ｌ　です。 まだまだ　暑い　日 が　続きます　ね。 "
       "でも　クーラー の　温度 の　下げすぎ は　体 に　よく　ありません。 "
       "今夜 は 「きんようとくべつ　ロードショー」と　題して。 皆さん　お待ち　かね。 "
       "「デスノートリライト　２　Ｌ を　継ぐ者」を　お送り　いたします。 続編　であり。 "
       "日本 に　行きます。 必ず　２７　度前後 に　して。 "
       "冷やしすぎない　ように　して　ください。 死んだ　あと。 "
       "昨年　放送　された 「リライト　幻視　する　神」の　続編　であり。 "
       "キラ と　私 の　後継　者 を　名乗る　人物 が　戦う　お話　だ　そう　です。 "
       "ここ から は　前作 を　ご覧 に　なって　いない　方 の　ため に。"
       "それ　じゃあ　ワタリ　始めて　くれ。 いい　くらい の　真面目な　優等生　だ　よ。 "
       "デスノート を　拾った　こと を　きっかけ に、 そして　ここ に　いて　唯一　名前 が　ない の が　"
       "あらゆる　人々 を　翻弄　する　孤高 の　殺人者。 "
       "キラ　事件　参考人　として　拘束　された が　"
       "その　最中　別 の　キラ が　現れ　動き　始めた　ため、 "
       "キラ　捜査 に　協力　する　こと と　なる。 新世界 の　神 と　なる。 "
       "Ｌ　キラ を　追う　世界　的　探偵。 キラ　事件　により　初めて　表舞台 に　立つ。 "
       "明晰な　頭脳 と　迅速な　行動力 で、 ノート に　名前 を　書いて も　死なない　という の は、 "
       "私 は　夜神君 を　キラ　じゃない　かと　疑って　いるんです。 "
       "かいつまん で　お話し　しましょう。 この　物語 の　主人公　夜　神月、 "
       "キラ　として　暗躍、 時に　協力し　時 に　傍観し、 持つ第　２ の　キラ。 "
       "女優 と　いった。 夜　神月 に　協力　する。 にっぽん　そうさほんぶ、 刑事たち、 "
       "混成　チーム で、 ヨツバ　という　企業 に、 そして　ついに、 "
       "そして　ついに　真 の　キラ と　目　された。 目 を　持つ第　２ の　キラ。 "
       "彼 の　ため　ならば　何でも　する　という　魔性 の　持ち主。 ノート　だ！　"
       "もう　お前 は　終わり　だ。 待って　いる の も　面倒　だ。 書け　こいつら を…　"
       "牢獄 に　入れられたんじゃ　いつ　死ぬ　か　分からない。 できる　掟　だ。 "
       "人間界 に　持ち込んだ。 月君　待て、 これ は　もう…　止血 を！　ここ で　死ね。 "
       "俺 が　お前 の　名前 を　俺 の　ノート に　書く　こと に　なる と、 "
       "海砂 は　どうした　高田 は？　ニア　終わりました　ね。 クソ…　何だ…　これ は…　"
       "言ったじゃない　です　か。 ノート に　細工　した　から　死にません と、 "
       "ノート に　名前 を　書いて も　死なない　という の は、 ニア が、 決まり　です。 "
       "言われた　とおり に　した と　言った。 ＳＰＫ の　本名 に　間違い　ありません。 "
       "自分たち の　目 で　確認　して　ください。 私 は　仰せ の　とおり に、 "
       "なぜ　死なない？　だから　死にません と　何度も　言った　はず　です。 "
       "撃つ　なら　僕以外 の　人間 を　撃て、 誰 を　撃ってる？　ふざける　な。 "
       "また　同じ　こと の　繰り返し で　悪い が　キラ が　捕まる　まで　君 を　監視　したい。 "
       "降りて　おもしれえ　事 を　して　きた　死神 が　いるって　よ。 もう　飽きた。 "
       "おもしれえって　聞いた　ぜ。 見てて も　つまん　ねえ。 "
       "変えよう　なんて　バカな　事 を　考えてる　奴　なら　おもしれえ　なあ。 "
       "何　だって？　あいつ に　会いたい？　会ったって　面白い　事　なんて"
       "あんた に　会い に　来た　者　だ。 タダ と は　言うわない。 こんな　ダルい　世界、 "
       "ばからい　だ。 奴 は、 受けて　当然 の、 犯した　者 が、 僕 は、 "
       "あんな の　見 に　行った　の？　おもしれえって　聞いた　ぜ。 "
       ; FIXME 'ここら に　いるって　聞いてんだ。 ' below doesn't work with this
       ;"ホント　金返せって　感じ。 使い　方、 あるって　いうんだ。 誰　だ？　"
       ;"何の　用 が　あるって　いうんだ？　"
       "まったく　病ん　でる　な〜　ここら に　いるって　聞いてんだ。 "
       "世の中 の　ため に、 いけないんだ。 奴　ばかり　だ。 世の中　腐って　いる。 "
       "世の中 を　変えて　やる。 誰か に　消されて　いる。 まあ　いい、 どいつも　こいつも、 "
       "いい　方向 に　すすんで　いく。 造り　上げて　いく。 すっげー　時間、 "
       "ホント　金返せって　感んじ。 "
       ; "そう　すれば　" 35 FIXME this doesn't work
       ;"心 の　優しい　人間 かで の　世界 を　作り　上げて　いく。 " 37 FIXME this doesn't work
       "同じ　事 の　繰り返し。 "))

(defn- filter-by-some-part-of-speech
  [n {:keys [partOfSpeech]} tokens]
  (let [take-n (partial take n)
        split-speech #(str/split % #",")
        partOfSpeech (split-speech partOfSpeech)
        equal-speech #(= (take-n partOfSpeech) (-> % :partOfSpeech take-n))
        f (comp equal-speech #(update % :partOfSpeech split-speech))]
  	(seq (filter f tokens))))

(defn- filter-by-surface
  [{:keys [surfaceForm]} tokens]
  (seq (filter #(= (:surfaceForm %) surfaceForm) tokens)))

(defmulti elect-by (fn [& args] (first args)))

(defmethod elect-by :surface-and-part-of-speech
  [_ n token tokens]
  (let [take-n #(take n %)]
  	(->> (filter-by-some-part-of-speech n token tokens)
  		   (filter-by-surface token))))

(defmethod elect-by :part-of-speech [_ n token tokens]
  (filter-by-some-part-of-speech n token tokens))

(defmethod elect-by :surface [_ token tokens]
  (filter-by-surface token tokens))

(defn find-token-candidates
  [token the-model]
  "Accepts token and model returning a list of token candidates."
  (or (some->> (elect-by :surface-and-part-of-speech 4 token the-model)
               (map #(assoc % :elect 1)))
      (some->> (elect-by :surface-and-part-of-speech 3 token the-model)
               (map #(assoc % :elect 2)))
      (some->> (elect-by :surface-and-part-of-speech 2 token the-model)
               (map #(assoc % :elect 3)))
      (some->> (elect-by :part-of-speech 4 token the-model)
               (map #(assoc % :elect 4)))
      (some->> (elect-by :part-of-speech 3 token the-model)
               (map #(assoc % :elect 5)))
      (some->> (elect-by :surface-and-part-of-speech 1 token the-model)
               (map #(assoc % :elect 6)))
      (some->> (elect-by :part-of-speech 2 token the-model)
               (map #(assoc % :elect 7)))
      (some->> (elect-by :part-of-speech 1 token the-model)
               (map #(assoc % :elect 8)))
      (some->> (elect-by :surface token the-model)
               (map #(assoc % :elect 9)))))

(defn ->narrowed-model-entry-for-nexts
  [next-token [model-prev-token model-next-token-map]]
  (let [next-token-candidates (find-token-candidates next-token
                                                     (keys model-next-token-map))
        next-token-candidates-no-elect (map #(dissoc % :elect) next-token-candidates)
        next-token-model-candidates
          (filter #(some #{(first %)} next-token-candidates-no-elect)
                  model-next-token-map)]
    (some->> next-token-model-candidates
             seq
             (vector model-prev-token)
             (vector (-> next-token-candidates first :elect)))))

(defn ->narrowed-model-for-nexts [next-token model]
  (->> (keep (partial ->narrowed-model-entry-for-nexts next-token) model)
       (sort-by first) ;=> [ [1 [..]] [1 [..]] [3 [..]] ]]
       (partition-by (comp identity first)) ;=> [ [[1 [..]] [1 [..]]] [[3 [..]]] ]
       first ;=> [[1 [..]] [1 [..]]]
       (map second)
       seq)) ;=> [[..] [..]]