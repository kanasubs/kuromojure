(ns kuro.test.fude
  (:use midje.sweet kuro.mojure kuro.fude))

(fact
  (some-space {:surfaceForm "　"}) => "　"
  (some-space {:surfaceForm "私"}) => nil)

(fact
  (inject-space-info {:surfaceForm "　"} {:surfaceForm "猫"})
  	=> [{:surfaceForm "　"} {:surfaceForm "猫" :prev-space "　"}])