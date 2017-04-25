kuromojure [![Build Status](https://travis-ci.org/ccfontes/kuromojure.png?branch=master)](https://travis-ci.org/ccfontes/kuromojure)
=======
Kuromojure is a general purpose, practical and concise Clojure wrapper around
[Kuromoji](https://github.com/atilika/kuromoji) (version 0.7.7), a Japanese
Morphological Analyser designed for search.

<b>Warning</b>: kuromojure is very unstable. The API will probably change some
times in the near future, so visit this page often to get the updates.

### Installing
-------
Add the following entry to the `:dependencies` vector of your `project.clj` file:
[![clojars version](https://clojars.org/kuromojure/latest-version.svg?raw=true)](https://clojars.org/kuromojure)

### Crafting
-------
Tokenizer modes are `:normal`, `:search` and `:extended`. Omitting
mode defaults to `:normal`.
```clj
user=> (use 'kuromojure)
user=> (with-tokenizer :search
         (-> "日本" tokenize first))
;;=> {:surface "日本"
;     :known true
;     :reading "ニッポン"
;     :features [:noun :proper :place :country nil nil "日本" "ニッポン" "ニッポン"]
;     :base-form "日本"
;     :part-of-speech [:noun :proper :place :country]}
```

### Wishlist
-------
- Upgrade Kuromoji to version 0.8.0 (will release Kuromojure 0.8.0 then)

### Repurposing the library
-------
The intermediate fns logic for translation applies here too.
Use `clj-tokenize` to tokenize the text into `clj-token's and from there do the
transformations you would like to the tokens:
```clj
(with-tokenizer
    (-> "日本" clj-tokenize first)) =>
;     {:surfaceForm "日本"
;      :known true
;      :reading "ニッポン"
;      :allFeatures "名詞,固有名詞,地域,国,*,*,日本,ニッポン,ニッポン"
;      :baseForm "日本"
;      :partOfSpeech "名詞,固有名詞,地域,国"
;      :position 0
;      :allFeaturesArray ["名詞" "固有名詞" "地域" "国" "*" "*" "日本" "ニッポン" "ニッポン"]
;      :class org.atilika.kuromoji.Token
;      :user false
;      :unknown false}
```

### Contributions
-------
- [Christian Moen](https://github.com/cmoen) continuously provided unvaluable
information, including the Part of Speech translations to English from Lucene.
- [Kuromoji example in Clojure](https://github.com/bouzuya/clj-kuromoji-example)
by [bouzuya](https://github.com/bouzuya)

### License
-------
Copyright (C) 2017 Carlos C. Fontes.

Double licensed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same
as Clojure) or the
[Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
