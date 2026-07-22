(ns marketentry.governor
  "Market-Entry Compliance Governor -- the independent compliance layer
  that earns the MarketEntry-LLM the right to commit. The LLM has no
  notion of Turkmenistan procurement law, whether a claimed engagement
  fee actually equals base + months x rate, or when a draft stops
  being a draft and becomes a real-world filing submission, so this
  MUST be a separate system able to *reject* a proposal and fall back
  to HOLD.

  `:itonami.blueprint/governor` is `:market-entry-compliance-governor`
  (shared family keyword on blueprints).

  This blueprint's own text (docs/business-model.md Trust Controls:
  'any actual filing submission requires Market-Entry Compliance
  Governor clearance and always escalates to human sign-off'; 'a false
  or fabricated regulatory-requirement claim is a HARD hold') names
  exactly the checks below.

  === Why FIVE checks, not the fleet's usual SIX-OR-MORE ===

  Every other iso3166 market-entry actor built so far in this fleet
  (AGO=7, LBY/GNB/SDN=6, ...) adds at least ONE additional flagship
  HARD conditional check beyond the structural floor -- a
  resident-entity boolean (AGO's `ao-entity-missing`), a maintained
  bank-balance floor (LBY's `office-balance-below-floor`), a
  commercial-registration boolean (GNB's `commercial-registration-
  missing`) -- each grounded in a fact SPECIFIC enough (a floor
  amount, an unambiguous engagement-level boolean requirement) to be
  independently re-verified against an engagement's own declared
  ground truth.

  Turkmenistan's own genuinely thin, low-transparency public record
  (see `marketentry.facts` namespace docstring) supported only THREE
  independently-grounded facts this iteration, at THREE different
  confidence levels (business registration: MODERATE; public
  procurement legal framework: HIGH; tax registration: LOWER) -- none
  of them specific enough to build an ADDITIONAL flagship conditional
  check without fabricating implied institutional detail this
  iteration could not verify (no dedicated procurement regulator name,
  no e-procurement portal, no FDI-screening body, no precise tax
  authority name beyond 'State Tax Service'). Padding the check count
  to match a richer sibling actor would itself be a fabrication --
  same discipline `cloud-itonami-iso3166-lby`'s/`-gnb`'s/`-sdn`'s own
  governors state explicitly, taken one step further here: TKM adds
  ZERO additional flagship conditional checks, landing at the fleet's
  bare structural floor.

  The three verified facts are NOT discarded -- they ground TKM's
  spec-basis citation (the 2014 Law on Tenders, the strongest of the
  three) and its THREE-item `:required-evidence` checklist (business
  registration + public procurement compliance + tax registration),
  both enforced by checks 1-2 below. They are documented, with their
  individual confidence levels, in `marketentry.facts` and README
  ('Why fewer checks').

  FIVE checks, in priority order, ALL HARD violations: a human
  approver CANNOT override them. The confidence/actuation gate is
  SOFT: it asks a human to look (low confidence / actuation), and the
  human may approve -- but see `marketentry.phase`: for `:stake
  :actuation/draft-filing`/`:actuation/submit-filing` NO phase ever
  allows auto-commit either. Two independent layers agree that
  actuation is always a human call.

    1. Spec-basis                  -- did the jurisdiction proposal
                                       cite an OFFICIAL source
                                       (`marketentry.facts`), or
                                       invent one? Grounded in the 2014
                                       Law on Tenders for TKM -- HIGH
                                       confidence, this catalog's
                                       strongest fact.
    2. Evidence incomplete         -- for `:filing/draft`/
                                       `:filing/submit`, has the
                                       jurisdiction actually been
                                       assessed with a full THREE-item
                                       evidence checklist on file
                                       (business registration + public
                                       procurement compliance + tax
                                       registration)?
    3. Engagement fee mismatch     -- for `:filing/submit`,
                                       INDEPENDENTLY recompute whether
                                       the engagement's own `:claimed-
                                       fee` equals `base-fee +
                                       monthly-rate x monitoring-
                                       months` -- honest reapplication
                                       of the ground-truth-recompute
                                       discipline sibling actors use.
                                       Jurisdiction-agnostic (this
                                       actor's own service-fee
                                       honesty, not a Turkmenistan-
                                       specific fact).
    4. Confidence floor / actuation
       gate                          -- LLM confidence below threshold,
                                       OR the op is `:filing/draft`/
                                       `:filing/submit` (REAL acts)
                                       -> escalate. (SOFT -- see
                                       above.)

  Two more guards, double-draft/double-submit prevention, are enforced
  off dedicated `:drafted?`/`:submitted?` facts (never a `:status`
  value) -- these plus items 1-3 above are the FIVE HARD violation
  functions `check` actually concatenates."
  (:require [marketentry.facts :as facts]
            [marketentry.registry :as registry]
            [marketentry.store :as store]))

(def confidence-floor 0.6)

(def high-stakes
  "Stakes grave enough to always require a human, even when clean.
  Drafting a real filing package and submitting a real portal
  registration are the two real-world actuation events this actor
  performs."
  #{:actuation/draft-filing :actuation/submit-filing})

;; ----------------------------- checks -----------------------------

(defn- spec-basis-violations
  "A `:jurisdiction/assess` (or `:filing/draft`/`:filing/submit`)
  proposal with no spec-basis citation is a HARD violation -- never
  invent a jurisdiction's market-entry requirements."
  [{:keys [op]} proposal]
  (when (contains? #{:jurisdiction/assess :filing/draft :filing/submit} op)
    (let [value (:value proposal)]
      (when (or (empty? (:cites proposal))
                (and (contains? value :spec-basis) (nil? (:spec-basis value))))
        [{:rule :no-spec-basis
          :detail "公式spec-basisの引用が無い提案は法域要件として扱えない"}]))))

(defn- evidence-incomplete-violations
  "For `:filing/draft`/`:filing/submit`, the jurisdiction's required
  registration evidence (TKM's THREE-item checklist: business
  registration/procurement compliance/tax registration) must actually
  be satisfied."
  [{:keys [op subject]} st]
  (when (contains? #{:filing/draft :filing/submit} op)
    (let [e (store/engagement st subject)
          assessment (store/assessment-of st subject)]
      (when-not (and assessment
                     (facts/required-evidence-satisfied?
                      (:jurisdiction e) (:checklist assessment)))
        [{:rule :evidence-incomplete
          :detail "法域の必要書類(法務省法人登記/2014年入札法遵守/国税庁HSB納税者番号)が充足していない状態での提案"}]))))

(defn- engagement-fee-mismatch-violations
  "For `:filing/submit`, INDEPENDENTLY recompute whether the
  engagement's own claimed fee equals base + months x rate."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (let [e (store/engagement st subject)]
      (when-not (registry/engagement-fee-matches-claim? e)
        [{:rule :engagement-fee-mismatch
          :detail (str subject " の申告手数料(" (:claimed-fee e)
                      ")が独立再計算値(" (registry/compute-engagement-fee e) ")と一致しない")}]))))

(defn- already-drafted-violations
  "For `:filing/draft`, refuses to draft the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/draft)
    (when (store/engagement-already-drafted? st subject)
      [{:rule :already-drafted
        :detail (str subject " は既にドラフト済み")}])))

(defn- already-submitted-violations
  "For `:filing/submit`, refuses to submit the SAME engagement twice."
  [{:keys [op subject]} st]
  (when (= op :filing/submit)
    (when (store/engagement-already-submitted? st subject)
      [{:rule :already-submitted
        :detail (str subject " は既に提出済み")}])))

(defn check
  "Censors a MarketEntry-LLM proposal against the governor rules.
  Returns {:ok? bool :violations [..] :confidence c :escalate? bool
  :high-stakes? bool :hard? bool}."
  [request _context proposal st]
  (let [hard (into []
                   (concat (spec-basis-violations request proposal)
                           (evidence-incomplete-violations request st)
                           (engagement-fee-mismatch-violations request st)
                           (already-drafted-violations request st)
                           (already-submitted-violations request st)))
        conf (:confidence proposal 0.0)
        low? (< conf confidence-floor)
        stakes? (boolean (high-stakes (:stake proposal)))
        hard? (boolean (seq hard))]
    {:ok?          (and (not hard?) (not low?) (not stakes?))
     :violations   hard
     :confidence   conf
     :hard?        hard?
     :escalate?    (and (not hard?) (or low? stakes?))
     :high-stakes? stakes?}))

(defn hold-fact
  "The audit fact written when a proposal is rejected (HOLD)."
  [request context verdict]
  {:t          :governor-hold
   :op         (:op request)
   :actor      (:actor-id context)
   :subject    (:subject request)
   :disposition :hold
   :basis      (mapv :rule (:violations verdict))
   :violations (:violations verdict)
   :confidence (:confidence verdict)})
