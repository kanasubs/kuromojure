kuromojure [![Build Status](https://travis-ci.org/ccfontes/kuromojure.png?branch=master)](https://travis-ci.org/ccfontes/kuromojure)
==========
Kuromojure is a practical and concise Clojure wrapper around
[kuromoji version 0.7.7](https://github.com/atilika/kuromoji), a Japanese Morphological
Analyser designed for search.

<b>Warning</b>: Kuromojure is very unstable. The API will probably change some times in
the near future, so visit this page often to get the updates.

### Installing
-------
[![clojars version](https://clojars.org/kuromojure/latest-version.svg?raw=true)]
(https://clojars.org/kuromojure)

### Crafting
-------
Tokenizer modes are `:normal`, `:search` or `:extended`. Omitting mode defaults
to `:normal`.
```clj
user=> (use 'kuromojure)
user=> (with-tokenizer :search
         (-> "日本" tokenize first))
;;=> {:surface "日本"
;     :known true
;     :reading "ニッポン"
;     :features ("名詞" "固有名詞" "地域" "国" "*" "*" "日本" "ニッポン" "ニッポン")
;     :base "日本"
;     :classes ["名詞" "固有名詞" "地域" "国"]}
```

### Translation to Japanese
-------
README.md and API translation (`defcopy`), anyone can do it please? It
can stay side by side with the English version, or even something more kakkoii!

### Missing something or you just don't agree? You have several options, and all are world changing
-------
Criticize my decisions.

Ask me to implement something nice.

Doing it yourself is amazing.

Fork it and be creative.

### Using kuromojure in Ramen or Disruptive projects?
-------
I'll be happy to add it to this list.

[kanasubs.com](http://www.kanasubs.com) — Convert raw subtitles in Kanji to
Kana online.

### Gentle contributions
-------
- [Kuromoji example in Clojure](https://github.com/bouzuya/clj-kuromoji-example)
by [bouzuya](https://github.com/bouzuya)

### License
-------
Copyright (C) 2014 Carlos C. Fontes.

Double licensed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same
as Clojure) or the
[Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
