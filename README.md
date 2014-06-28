kuromojure [![Build Status](https://travis-ci.org/ccfontes/kuromojure.png?branch=master)](https://travis-ci.org/ccfontes/kuromojure)
==========
A Clojure Japanese Morphological Analyser specialized for search.

Kuromojure is a general purpose, practical and concise Clojure wrapper around
[Kuromoji](https://github.com/atilika/kuromoji) (version 0.7.7), a Japanese
Morphological Analyser designed for search.

<b>Warning</b>: kuromojure is very unstable. The API will probably change some
times in the near future, so visit this page often to get the updates.

### Installing
-------
Add the following to the `:dependencies` vector of your `project.clj` file:
[![clojars version](https://clojars.org/kuromojure/latest-version.svg?raw=true)]
(https://clojars.org/kuromojure)

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
For more examples, read the [tests](https://github.com/ccfontes/kuromojure/blob/master/test/kuromojure_tests.clj).

### Goals
-------
Kuromojure is already useful for end user applications. It aims further to
become the starting point of Kuromoji in Clojure by repurposing it to meet
other ends.

### Wishlist
-------
- Upgrade Kuromoji to version 0.80 (will make it for release 0.8.0 haha)

### Translating to Japanese
I think the best idea would be to use this project as a dependency of a
separate project and make the translation there. For that purpose, I provided
intermediate `token` and `tokenize` transform fns. Please take a look at the
source. If you have a different idea, please drop me a line and I'll adjust the
API.

### Repurposing the library
The intermediate fns logic for translation applies here too.

### Missing something or you just don't agree?
-------
Please criticize my decisions.

Ask me to implement something nice.

Doing it yourself is amazing.

Fork it and be creative :)

### Using kuromojure in Ramen or Disruptive projects?
-------
I'll be happy to add it to this list.

[kanasubs.com](http://www.kanasubs.com) — Convert raw subtitles in Kanji to
Kana online.

### Gentle contributions
-------
- [Christian Moen](https://github.com/cmoen) continuously provided unvaluable
information, including the Part of Speech translations to English
- [Kuromoji example in Clojure](https://github.com/bouzuya/clj-kuromoji-example)
by [bouzuya](https://github.com/bouzuya)

### License
-------
Copyright (C) 2014 Carlos C. Fontes.

Double licensed under the
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same
as Clojure) or the
[Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
