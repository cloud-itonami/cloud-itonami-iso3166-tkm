(ns marketentry.governor-contract-test
  "The governor contract as executable tests -- this vertical's own
  Trust Controls implemented faithfully. The single invariant under test:

    MarketEntry-LLM never drafts or submits a filing the Market-Entry
    Compliance Governor would reject, `:filing/draft`/`:filing/submit`
    NEVER auto-commit at any phase, `:engagement/intake` MAY auto-commit
    when clean, and every decision (commit OR hold) leaves exactly one
    ledger fact.

  TKM's governor is deliberately FIVE HARD violation functions (not
  the fleet's usual six-or-more, see `marketentry.governor` docstring)
  -- no AGO-style/LBY-style/GNB-style flagship conditional check, so
  there is no analogous 'ao-entity-missing'/'nif-unverified' test
  here. What TKM's genuinely thin fact base DOES support -- spec-basis
  citation, evidence-checklist completeness, engagement-fee-mismatch,
  the double-actuation guards, and human-gated actuation -- is tested
  below exactly as faithfully as any richer sibling actor's suite."
  (:require [clojure.test :refer [deftest is testing]]
            [langgraph.graph :as g]
            [marketentry.store :as store]
            [marketentry.operation :as op]))

(defn- fresh []
  (let [db (store/seed-db)]
    [db (op/build db)]))

(def operator {:actor-id "op-1" :actor-role :market-entry-operator :phase 3})

(defn- exec-op [actor tid request context]
  (g/run* actor {:request request :context context} {:thread-id tid}))

(defn- approve! [actor tid]
  (g/run* actor {:approval {:status :approved :by "op-1"}} {:thread-id tid :resume? true}))

(defn- assess!
  [actor tid-prefix subject]
  (exec-op actor (str tid-prefix "-assess") {:op :jurisdiction/assess :subject subject} operator)
  (approve! actor (str tid-prefix "-assess")))

(defn- draft!
  [actor tid-prefix subject]
  (exec-op actor (str tid-prefix "-draft") {:op :filing/draft :subject subject} operator)
  (approve! actor (str tid-prefix "-draft")))

(deftest clean-intake-auto-commits
  (let [[db actor] (fresh)
        res (exec-op actor "t1"
                  {:op :engagement/intake :subject "eng-1"
                   :patch {:id "eng-1" :operator "Kita Systems TM"}} operator)]
    (is (= :commit (get-in res [:state :disposition])))
    (is (= "Kita Systems TM" (:operator (store/engagement db "eng-1"))) "SSoT actually updated")
    (is (= 1 (count (store/ledger db))))))

(deftest jurisdiction-assess-always-needs-approval
  (testing "assess is never in any phase's :auto set -- always human approval, even when clean"
    (let [[db actor] (fresh)
          res (exec-op actor "t2" {:op :jurisdiction/assess :subject "eng-1"} operator)]
      (is (= :interrupted (:status res)))
      (let [r2 (approve! actor "t2")]
        (is (= :commit (get-in r2 [:state :disposition])))
        (is (some? (store/assessment-of db "eng-1")))))))

(deftest fabricated-jurisdiction-is-held
  (testing "CHECK 1 (spec-basis): a jurisdiction/assess proposal with no official spec-basis -> HOLD"
    (let [[db actor] (fresh)
          res (exec-op actor "t3"
                    {:op :jurisdiction/assess :subject "eng-1" :no-spec? true} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:no-spec-basis} (-> (store/ledger db) first :basis)))
      (is (nil? (store/assessment-of db "eng-1")) "no assessment written"))))

(deftest draft-without-assessment-is-held
  (testing "CHECK 2 (evidence-incomplete): filing/draft before any jurisdiction assessment -> HOLD"
    (let [[db actor] (fresh)
          res (exec-op actor "t4" {:op :filing/draft :subject "eng-1"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:evidence-incomplete} (-> (store/ledger db) first :basis))))))

(deftest partial-evidence-checklist-is-held
  (testing "CHECK 2 (evidence-incomplete): an assessment written with an INCOMPLETE checklist (fewer than TKM's three required-evidence items) still HOLDs on filing/draft"
    (let [[db actor] (fresh)]
      (exec-op actor "t4b" {:op :engagement/intake :subject "eng-1"
                            :patch {:id "eng-1"}} operator)
      ;; commit a deliberately-partial assessment straight to the SSoT,
      ;; bypassing the advisor, to exercise evidence-incomplete-violations
      ;; in isolation from spec-basis-violations.
      (store/commit-record! db {:effect :assessment/set :path ["eng-1"]
                                :payload {:jurisdiction "TKM"
                                          :checklist ["Ministry of Justice business-registration record (Department of state registration of legal entities, minjust.gov.tm -- MODERATE confidence: secondary/commercial sources plus one state-affiliated news outlet, not independently read from the primary government site this session)"]
                                          :spec-basis "x"}})
      (let [res (exec-op actor "t4c" {:op :filing/draft :subject "eng-1"} operator)]
        (is (= :hold (get-in res [:state :disposition])))
        (is (some #{:evidence-incomplete} (-> (store/ledger db) last :basis)))))))

(deftest engagement-fee-mismatch-is-held
  (testing "CHECK 3 (engagement-fee-mismatch): claimed fee that doesn't equal base + months x rate -> HOLD"
    (let [[db actor] (fresh)
          _ (assess! actor "t6pre" "eng-3")
          _ (draft! actor "t6pre" "eng-3")
          res (exec-op actor "t6" {:op :filing/submit :subject "eng-3"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:engagement-fee-mismatch} (-> (store/ledger db) last :basis)))
      (is (empty? (store/submit-history db))))))

(deftest submit-always-escalates-then-human-decides
  (testing "a clean fully-assessed submit still ALWAYS interrupts for human approval"
    (let [[db actor] (fresh)
          _ (assess! actor "t8pre" "eng-1")
          _ (draft! actor "t8pre" "eng-1")
          r1 (exec-op actor "t8" {:op :filing/submit :subject "eng-1"} operator)]
      (is (= :interrupted (:status r1)) "pauses for human approval even when governor-clean")
      (testing "approve -> commit, submit record drafted"
        (let [r2 (approve! actor "t8")]
          (is (= :commit (get-in r2 [:state :disposition])))
          (is (true? (:submitted? (store/engagement db "eng-1"))))
          (is (= 1 (count (store/submit-history db))) "one draft submit record"))))))

(deftest draft-always-escalates-then-human-decides
  (testing "a clean fully-assessed draft still ALWAYS interrupts for human approval"
    (let [[db actor] (fresh)
          _ (assess! actor "t9pre" "eng-1")
          r1 (exec-op actor "t9" {:op :filing/draft :subject "eng-1"} operator)]
      (is (= :interrupted (:status r1)) "pauses for human approval even when governor-clean")
      (testing "approve -> commit, draft record drafted"
        (let [r2 (approve! actor "t9")]
          (is (= :commit (get-in r2 [:state :disposition])))
          (is (true? (:drafted? (store/engagement db "eng-1"))))
          (is (= 1 (count (store/draft-history db))) "one draft record"))))))

(deftest engagement-double-draft-is-held
  (testing "double-actuation guard: drafting the same engagement twice -> HOLD on the second attempt"
    (let [[db actor] (fresh)
          _ (assess! actor "t10pre" "eng-1")
          _ (draft! actor "t10pre" "eng-1")
          res (exec-op actor "t10" {:op :filing/draft :subject "eng-1"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:already-drafted} (-> (store/ledger db) last :basis)))
      (is (= 1 (count (store/draft-history db))) "still only the one earlier draft"))))

(deftest engagement-double-submit-is-held
  (testing "double-actuation guard: submitting the same engagement twice -> HOLD on the second attempt"
    (let [[db actor] (fresh)
          _ (assess! actor "t11pre" "eng-1")
          _ (draft! actor "t11pre" "eng-1")
          _ (exec-op actor "t11a" {:op :filing/submit :subject "eng-1"} operator)
          _ (approve! actor "t11a")
          res (exec-op actor "t11" {:op :filing/submit :subject "eng-1"} operator)]
      (is (= :hold (get-in res [:state :disposition])))
      (is (some #{:already-submitted} (-> (store/ledger db) last :basis)))
      (is (= 1 (count (store/submit-history db))) "still only the one earlier submit"))))

(deftest every-decision-leaves-one-ledger-fact
  (testing "write-only-through-ledger: N operations -> N ledger facts"
    (let [[db actor] (fresh)]
      (exec-op actor "a" {:op :engagement/intake :subject "eng-1"
                          :patch {:id "eng-1" :operator "Kita Systems TM"}} operator)
      (exec-op actor "b" {:op :jurisdiction/assess :subject "eng-1" :no-spec? true} operator)
      (is (= 2 (count (store/ledger db)))
          "one commit + one hold, both recorded"))))

(deftest filing-submit-never-auto-commits-even-when-clean
  (testing "structural invariant: filing/draft AND filing/submit NEVER auto-commit at any phase, even fully clean/high-confidence"
    (let [[_db actor] (fresh)
          _ (assess! actor "t12pre" "eng-1")
          r1 (exec-op actor "t12" {:op :filing/draft :subject "eng-1"} operator)]
      (is (= :interrupted (:status r1)))
      (let [_ (approve! actor "t12")
            r2 (exec-op actor "t13" {:op :filing/submit :subject "eng-1"} operator)]
        (is (= :interrupted (:status r2)))))))
