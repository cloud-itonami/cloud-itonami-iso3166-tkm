# cloud-itonami-iso3166-tkm

**`:implemented`** for **TKM** (Turkmenistan). MarketEntry-LLM Advisor
⊣ Market-Entry Compliance Governor, a real compiled
`kotoba-lang/langgraph` StateGraph (`:intake -> :advise -> :govern ->
:decide -+-> :commit / :request-approval -> :commit / :hold`),
append-only audit ledger.

```
clojure -M:dev:test
```

## The actor's 3 checks

Turkmenistan is one of the world's most closed, least transparent
economies. This iteration's own research found only **three**
well-grounded, independently-corroborated facts about its market-entry
compliance surface -- each at a **different, explicitly documented
confidence level**. Unlike this fleet's other iso3166 actors (which
typically ground 4-6 required-evidence items and at least one
additional flagship governor check), this actor deliberately does
**not** pad its fact base to match richer siblings. A smaller, honest
catalog beats forcing in institutional specificity that could not be
independently verified.

| # | Check | Owner authority | Confidence | Source |
|---|-------|------------------|------------|--------|
| 1 | Business registration | Ministry of Justice, Department of state registration of legal entities (minjust.gov.tm) | **Moderate** | Secondary/commercial sources plus one state-affiliated news outlet -- NOT independently read from the primary government site this session |
| 2 | Public procurement legal framework | Public-sector contracting authorities under the Law on Tenders | **High** (the strongest fact for this jurisdiction) | Law on Tenders for the Supply of Goods, Works and Services for State Needs, adopted 20 December 2014, effective 1 July 2015 -- independently corroborated by historical U.S. Department of State documentation *and* an ICNL library citation of the same title/dates |
| 3 | Tax registration | State Tax Service -- *hususy salgyt belgisi* (HSB), 12-digit Taxpayer Identification Number | **Lower** | Expat/freelancer tax-advisory blogs only -- not government-sourced |

These three facts are `marketentry.facts/catalog`'s `:required-evidence`
checklist, enforced by the governor's `evidence-incomplete-violations`
check on every `:filing/draft`/`:filing/submit` proposal, and fact #2
grounds the catalog's primary `:legal-basis`/`:provenance` cited by
`spec-basis-violations`.

### Why fewer checks

Every other iso3166 market-entry actor built so far in this fleet adds
at least one additional *flagship* HARD conditional check beyond the
structural floor -- a resident-entity boolean, a maintained
bank-balance floor, a commercial-registration boolean -- each grounded
in a fact specific enough (an exact floor amount, an unambiguous
engagement-level requirement) to be independently re-verified against
an engagement's own declared ground truth.

None of TKM's three facts clears that bar: business registration is
sourced only from secondary/commercial material (moderate confidence,
no primary-source detail on what "verified" would concretely mean),
and tax registration is sourced only from expat blogs (lower
confidence, same problem). Building an `ao-entity-missing`-style or
`nif-unverified`-style conditional check on top of either would require
inventing engagement-level verification semantics this iteration could
not independently confirm -- exactly the kind of fabrication this fleet's
`ZERO FABRICATION` discipline forbids. So `marketentry.governor` stays
at the fleet's bare structural floor: **five** HARD violation
functions (spec-basis, evidence-incomplete, engagement-fee-mismatch,
already-drafted, already-submitted) -- below every sibling actor built
so far (AGO=7, LBY/GNB/SDN=6). This is a deliberate, documented design
choice for this jurisdiction's genuinely thinner fact base, not an
oversight or a shortcut.

Explicitly **not** claimed, to guard against fabrication and against
cross-contamination with Tajikistan's (`cloud-itonami-iso3166-tjk`,
this session's other Central Asian "-stan" build, visually/
phonetically confusable with Turkmenistan) similarly-structured
catalog:

- No sub-agency/department beyond "Ministry of Justice" for business
  registration.
- No dedicated, independently-named procurement regulatory agency
  distinct from generic contracting-authority involvement.
- No precise legal name for the tax authority beyond "State Tax
  Service".
- No World Bank "Doing Business" ranking (program discontinued, often
  excluded Turkmenistan even when active).
- No WTO accession/GPA-related legal-framework document -- Turkmenistan
  is **not** a WTO member, Observer only since 22 July 2020.
- No e-procurement portal name/URL -- none was found (unlike most
  sibling jurisdictions).
- No named foreign-investment-screening body or law -- none was
  independently verified.

## Actuation

`:filing/draft` and `:filing/submit` are real-world portal-facing acts
(preparing/submitting a Turkmenistan market-entry filing package).
**Neither ever auto-commits, at any rollout phase** (see
`marketentry.phase` -- deliberately absent from every phase's `:auto`
set, a permanent structural fact) **and neither can be committed
without going through `interrupt-before #{:request-approval}`** (see
`marketentry.operation`), which pauses the StateGraph via its
checkpointer until a human market-entry operator supplies
`{:status :approved :by ...}`. `marketentry.governor`'s
`:actuation/draft-filing`/`:actuation/submit-filing` high-stakes gate
enforces the same invariant independently -- two layers, not one,
agree that actuation is always a human call. Every commit AND every
hold appends exactly one immutable fact to the audit ledger
(`store/append-ledger!`) -- the ledger is append-only on both the
`MemStore` and `DatomicStore` backends, proven equivalent by a shared
store-contract test.

AGPL-3.0-or-later.

## Culture catalog

Alongside the market-entry / statute catalogs, this repo carries a
**country-level regional-culture catalog** (ADR-2607171400 addendum 2,
`cloud-itonami-municipality-culture-catalog` Wave 1, in
`com-junkawasaki/root`) — national dishes, protected products, beverages,
crafts, festivals and heritage sites for Turkmenistan:

- `src/culture/facts.cljc` — the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` — DataScript schema.
- `data/culture-tx.edn` — derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis — never fabricate one.
