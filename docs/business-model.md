# Business model — TKM

Independent market-entry compliance for Turkmenistan, deliberately
scoped to a genuinely thin, low-transparency public record.

- Turkmenistan is one of the world's most closed economies. This
  iteration's own research found only three well-grounded,
  independently-corroborated facts: Ministry of Justice business
  registration (moderate confidence), the 2014 Law on Tenders public-
  procurement legal framework (high confidence -- the strongest fact),
  and State Tax Service *hususy salgyt belgisi* (HSB) tax registration
  (lower confidence, expat/freelancer tax-advisory blogs only). See
  `src/marketentry/facts.cljc` for the full sourcing discussion and the
  explicit list of claims this iteration deliberately did NOT make
  (no named e-procurement portal, no FDI-screening body, no dedicated
  procurement regulator, no WTO accession documentation -- Turkmenistan
  is an Observer, not a member).
- No verifiable national e-procurement portal was found for
  Turkmenistan, unlike most sibling jurisdictions in this fleet -- not
  claimed, not invented.
- The Market-Entry Compliance Governor is intentionally **five** HARD
  checks (not the fleet's usual six-or-more) -- no additional flagship
  conditional check (no resident-entity boolean, no bank-balance floor,
  no commercial-registration boolean) was added, because none of the
  three facts is specific enough to support one without fabricating
  implied institutional detail. See README "Why fewer checks".

## Trust Controls

Any actual filing submission requires Market-Entry Compliance Governor
clearance and always escalates to human sign-off. A false or
fabricated regulatory-requirement claim is a HARD hold that no human
approval can override.
