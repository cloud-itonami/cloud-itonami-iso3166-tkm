(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest tkm-has-spec-basis
  (let [sb (facts/spec-basis "TKM")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/business-registration-spec-basis "TKM")))
    (is (some? (facts/corporate-number-spec-basis "TKM")))))

(deftest tkm-has-exactly-three-required-evidence-items
  (testing "the genuinely thin, low-confidence TKM fact base yields exactly THREE required-evidence items, not the usual 4-6"
    (let [sb (facts/spec-basis "TKM")]
      (is (= 3 (count (:required-evidence sb)))))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "TKM")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "TKM" all)))
    (is (not (facts/required-evidence-satisfied? "TKM" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["TKM" "ATL"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))

(deftest business-registration-spec-basis-shape
  (let [rb (facts/business-registration-spec-basis "TKM")]
    (is (= "Ministry of Justice, Department of state registration of legal entities"
           (:business-registration-owner-authority rb)))
    (is (nil? (facts/business-registration-spec-basis "ATL")))))

(deftest corporate-number-spec-basis-shape
  (let [cn (facts/corporate-number-spec-basis "TKM")]
    (is (= "State Tax Service" (:corporate-number-owner-authority cn)))
    (is (nil? (facts/corporate-number-spec-basis "ATL")))))
