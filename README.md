kuromojure
==========
Kuromojure is a Clojure wrapper for [kuromoji](https://github.com/atilika/kuromoji).

Install
-------
[![clojars version](https://clojars.org/kuromojure/latest-version.svg?raw=true)](https://clojars.org/kuromojure)

Usage
-------
Tokenizer modes are `:normal`, `:search` or `:extended`. Omitting mode defaults to `:normal`.
```clj
user=> (use 'kuromojure)
user=> (with-tokenizer :search
         (-> "何を言ってるんだ" tokenize first))
;;=> {:base-form "何"
;     :position 0
;     :known true
;     :class org.atilika.kuromoji.Token
;     :reading "ナニ"
;     :part-of-speech "名詞,代名詞,一般,*"
;     :surface-form "何"
;     :all-features "名詞,代名詞,一般,*,*,*,何,ナニ,ナニ"
;     :user false
;     :unknown false
;     :all-features-array ("名詞" "代名詞" "一般" "*" "*" "*" "何" "ナニ" "ナニ")}
```

Missing something?
-------
Feature and pull requests are welcome!

Using kuromojure?
-------
I'll be happy to add your project using kuromojure to this list.

[kanasubs.com](http://www.kanasubs.com) — Convert raw subtitles in Kanji to Kana online.

Contributions
-------
- [Kuromoji example in Clojure](https://github.com/bouzuya/clj-kuromoji-example) by [bouzuya](https://github.com/bouzuya)
- The remainder by [Carlos Cunha](https://github.com/ccfontes)

License
-------
Copyright (C) 2014 Carlos C. Fontes.

Double licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure) or
the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
