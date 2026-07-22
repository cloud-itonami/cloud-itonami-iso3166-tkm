(ns marketentry.facts
  "Per-jurisdiction market-entry regulatory catalog -- the G2-style
  spec-basis table the Market-Entry Compliance Governor checks every
  `:jurisdiction/assess` proposal against ('did the advisor cite an
  OFFICIAL public source for this jurisdiction's requirements, or did
  it invent one?').

  Turkmenistan (TKM) is one of the world's most closed, least
  transparent economies. This iteration's own research could
  independently ground only THREE facts -- each carrying its OWN,
  explicitly different confidence level -- and deliberately does NOT
  pad the catalog with unverifiable specificity to match richer
  sibling catalogs' fact counts. Consistent with
  `cloud-itonami-iso3166-lby`'s own stated discipline ('a smaller,
  honest set of checks beats forcing in a check shape that isn't
  actually documented'), this catalog goes one step further: none of
  the three facts below is independently specific enough (a bank-
  balance floor, a resident-entity boolean, a named sub-agency) to
  support an ADDITIONAL flagship HARD governor conditional check the
  way AGO/LBY/GNB/SDN each add one -- so `marketentry.governor` stays
  at the fleet's structural floor (spec-basis + evidence-incomplete +
  engagement-fee-mismatch + the two double-actuation guards, FIVE HARD
  violation functions, below every other sibling actor's SIX-OR-MORE).
  This is a deliberate design choice for this jurisdiction's genuinely
  thinner fact base, not an oversight -- see README 'Why fewer checks'.

  The three facts (README calls these 'the actor's 3 checks' -- three
  distinct regulatory domains, each independently sourced and
  confidence-rated, together forming `:required-evidence` below):

  1. BUSINESS REGISTRATION (MODERATE confidence) -- Ministry of
     Justice (minjust.gov.tm), which has introduced an automated
     system for filing electronic applications for legal-entity
     registration, via its Department of state registration of legal
     entities. Sourced mainly from secondary/commercial sources plus
     one state-affiliated news outlet, NOT independently read from the
     primary government site this session -- do not treat this as
     government-primary-sourced.

  2. PUBLIC PROCUREMENT LEGAL FRAMEWORK (HIGH confidence -- the
     best-grounded fact for this jurisdiction) -- Law on Tenders for
     the Supply of Goods, Works and Services for State Needs, adopted
     20 December 2014, effective 1 July 2015. Independently
     corroborated by historical U.S. Department of State Investment
     Climate Statement documentation AND an International Center for
     Not-for-Profit Law (ICNL) library citation of the same title and
     dates -- two independent sources agreeing on title and dates is
     why this is the strongest fact in this catalog. This is the
     catalog's primary `:legal-basis`/`:provenance` -- what
     `:jurisdiction/assess` actually cites for TKM.

  3. TAX REGISTRATION (LOWER confidence) -- the State Tax Service
     issues a Taxpayer Identification Number locally called *hususy
     salgyt belgisi* (HSB), 12 digits. Sourced ONLY from expat/
     freelancer tax-advisory blogs this session, NOT government-
     sourced -- treat with real skepticism, the lowest-confidence fact
     in this catalog.

  EXPLICITLY NOT claimed (fabrication traps this iteration avoided):
  no sub-agency/department beyond \"Ministry of Justice\" for business
  registration; no dedicated, independently-named procurement
  regulatory agency distinct from generic contracting-authority
  involvement; no precise legal name for the tax authority beyond
  \"State Tax Service\"; no World Bank Doing Business ranking (the
  program is discontinued and often excluded Turkmenistan even when
  active); no WTO accession/GPA legal-framework citation (Turkmenistan
  is NOT a WTO member -- Observer only since 22 July 2020 -- so no WTO
  accession transparency documentation exists for this jurisdiction);
  no named e-procurement portal (none found, unlike most sibling
  jurisdictions); no named foreign-investment-screening body or law
  (none independently verified for Turkmenistan).

  CROSS-CONTAMINATION GUARD: Turkmenistan and Tajikistan
  (`cloud-itonami-iso3166-tjk`) are the two most visually/phonetically
  confusable '-stan' country names in Central Asia, with structurally
  analogous institutions. Any future TKM fact naming a procurement-
  portal URL, an FDI-screening body, or a confidently-named TIN-issuing
  authority beyond the State Tax Service/HSB pairing above should be
  treated as SUSPECT and cross-checked against this docstring (and
  against `cloud-itonami-iso3166-tjk`'s own catalog, to rule out an
  accidental cross-import) before being trusted or merged.

  Coverage is reported HONESTLY (see `coverage`): a jurisdiction not in
  this table has NO spec-basis, full stop -- the advisor must not
  fabricate one, and the governor holds if it tries.")

(def catalog
  "iso3 -> requirement map. `:required-evidence` mirrors the generic
  intake/portal-registration/filing evidence set -- here exactly THREE
  items, one per independently-verified fact (see namespace docstring).
  `:legal-basis`/`:owner-authority`/`:provenance` are the G2 citation
  the governor requires before any `:jurisdiction/assess` proposal can
  commit; TKM's is grounded in the 2014 Law on Tenders, the strongest
  of the three facts. TKM deliberately carries NO `:rep-*` keys (no
  resident-representative regime was independently verified) --
  `:business-registration-*` and `:corporate-number-*` are exposed as
  DOCUMENTED info (see `business-registration-spec-basis` /
  `corporate-number-spec-basis`) but are NOT wired into an additional
  HARD governor conditional check, because their confidence and
  specificity are too thin to support one without fabricating implied
  institutional detail (see namespace docstring)."
  {"TKM" {:name "Turkmenistan"
          :owner-authority "Public-sector contracting authorities operating under the 2014 Law on Tenders. No dedicated, independently-named procurement regulatory agency distinct from generic contracting-authority involvement was verified this iteration -- see namespace docstring"
          :legal-basis "Law on Tenders for the Supply of Goods, Works and Services for State Needs (adopted 20 December 2014, effective 1 July 2015) -- HIGH confidence, independently corroborated by historical U.S. Department of State Investment Climate Statement documentation and an International Center for Not-for-Profit Law (ICNL) library citation of the same title and dates"
          :national-spec "No independently-verified national e-procurement portal for Turkmenistan was found this iteration (unlike most sibling jurisdictions) -- not claimed, not invented"
          :provenance "Historical U.S. Department of State Investment Climate Statement (Turkmenistan) + International Center for Not-for-Profit Law (ICNL) library citation, both independently naming 'Law on Tenders for the Supply of Goods, Works and Services for State Needs', adopted 20 December 2014, effective 1 July 2015. Not independently re-fetched from a live Turkmenistan government legal portal this session -- none was located"
          :required-evidence ["Ministry of Justice business-registration record (Department of state registration of legal entities, minjust.gov.tm -- MODERATE confidence: secondary/commercial sources plus one state-affiliated news outlet, not independently read from the primary government site this session)"
                               "2014 Law on Tenders public-procurement compliance record (HIGH confidence -- independently corroborated by historical U.S. State Department documentation and an ICNL library citation of the same law title and dates)"
                               "State Tax Service hususy salgyt belgisi (HSB) 12-digit Taxpayer Identification Number record (LOWER confidence -- sourced only from expat/freelancer tax-advisory blogs this session, not government-sourced)"]
          :business-registration-owner-authority "Ministry of Justice, Department of state registration of legal entities"
          :business-registration-legal-basis "Ministry of Justice has introduced an automated system for filing electronic applications for legal-entity registration (MODERATE confidence -- secondary/commercial sources plus one state-affiliated news outlet, not independently read from minjust.gov.tm this session)"
          :business-registration-provenance "minjust.gov.tm (domain named in secondary sources; page contents not independently fetched/read this session)"
          :corporate-number-owner-authority "State Tax Service"
          :corporate-number-legal-basis "hususy salgyt belgisi (HSB), a 12-digit Taxpayer Identification Number (LOWER confidence -- expat/freelancer tax-advisory blogs only, not government-sourced -- the lowest-confidence fact in this catalog)"
          :corporate-number-provenance "Expat/freelancer tax-advisory blogs (not individually named or re-verified this session; not government-sourced)"}})

(defn spec-basis
  "The jurisdiction's requirement map, or nil -- nil means NO
  spec-basis, and the governor must hold any proposal that tries to
  assess or file on it."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report: how many of the requested jurisdictions
  actually have a spec-basis entry. Never report a missing
  jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-tkm R0: " (count catalog)
                 " jurisdiction seeded with an official spec-basis, "
                 "deliberately THREE required-evidence items (not the "
                 "usual 4-6) because Turkmenistan's own genuinely thin, "
                 "low-transparency public record supported only three "
                 "independently-grounded facts this iteration. Extend "
                 "`marketentry.facts/catalog`, never fabricate a "
                 "jurisdiction's requirements.")})))

(defn required-evidence-satisfied?
  "Does `submitted` (a set/coll of evidence keywords or strings)
  satisfy every evidence item listed for `iso3`? Missing spec-basis ->
  never satisfied."
  [iso3 submitted]
  (when-let [{:keys [required-evidence]} (spec-basis iso3)]
    (let [need (count required-evidence)
          have (count (filter (set submitted) required-evidence))]
      (= need have))))

(defn evidence-checklist [iso3] (:required-evidence (spec-basis iso3) []))

(defn business-registration-spec-basis
  "The jurisdiction's business (state) registration regime, or nil.
  Turkmenistan's is the Ministry of Justice's Department of state
  registration of legal entities -- MODERATE confidence (see namespace
  docstring). Documented here for transparency/testability; NOT wired
  into an additional HARD governor conditional check (see
  `marketentry.governor` docstring for why)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:business-registration-owner-authority sb)
      (select-keys sb [:business-registration-owner-authority
                       :business-registration-legal-basis
                       :business-registration-provenance]))))

(defn corporate-number-spec-basis
  "The jurisdiction's corporate-number / tax-id regime, or nil.
  Turkmenistan's is the State Tax Service's hususy salgyt belgisi
  (HSB) -- LOWER confidence, expat/freelancer tax-advisory blogs only
  (see namespace docstring). Documented here for transparency/
  testability; NOT wired into an additional HARD governor conditional
  check (see `marketentry.governor` docstring for why)."
  [iso3]
  (when-let [sb (spec-basis iso3)]
    (when (:corporate-number-owner-authority sb)
      (select-keys sb [:corporate-number-owner-authority
                       :corporate-number-legal-basis
                       :corporate-number-provenance]))))
