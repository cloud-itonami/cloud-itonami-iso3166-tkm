(ns culture.facts
  "Country-level regional-culture catalog for Turkmenistan (TKM) -- national
  dishes, protected products, beverages, crafts, festivals and heritage
  sites, per ADR-2607171400 addendum 2 (cloud-itonami-municipality-
  culture-catalog Wave 1, in com-junkawasaki/root). Sibling namespace to
  `marketentry.facts` / `statute.facts` (ADR-2607141700); city-level
  counterparts live in the cloud-itonami-municipality-* repos.

  Catalog is keyed by UPPERCASE ISO3 (mirrors `statute.facts`); entries
  carry no :culture/municipality (that attribute is city-level only).

  Every entry cites a source URL that was actually fetched and read on
  :culture/retrieved-at -- never fabricated. Summaries state only what the
  cited source confirms. An item not in this table has NO spec-basis, full
  stop; extend `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of culture entries."
  {"TKM"
   [{:culture/id "tkm.dish.dograma"
     :culture/name "Dograma"
     :culture/country "TKM"
     :culture/kind :dish
     :culture/summary "Central Asian dish of finely chopped meat, bread and onion broth, known as dograma in Turkmenistan (naryn in Xinjiang/Uzbekistan/Kyrgyzstan/Kazakhstan, turama in Karakalpakstan/Dagestan)."
     :culture/url "https://en.wikipedia.org/wiki/Beshbarmak"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.dish.plov"
     :culture/name "Plov"
     :culture/name-local "Palaw"
     :culture/country "TKM"
     :culture/kind :dish
     :culture/summary "Turkmen version of the Central Asian rice pilaf, called palaw, cooked with mutton, carrots, onions and garlic in a large cast-iron cauldron."
     :culture/url "https://en.wikipedia.org/wiki/Turkmen_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.dish.shurpa"
     :culture/name "Shurpa"
     :culture/name-local "Şurpa"
     :culture/country "TKM"
     :culture/kind :dish
     :culture/summary "Meat-bouillon soup called şurpa that serves as a soup base in Turkmen cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Turkmen_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.dish.manty"
     :culture/name "Manty"
     :culture/country "TKM"
     :culture/kind :dish
     :culture/summary "Steamed dumplings filled with ground meat, onions or pumpkin, part of Turkmen cuisine."
     :culture/url "https://en.wikipedia.org/wiki/Turkmen_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.beverage.chal"
     :culture/name "Chal"
     :culture/name-local "Çal"
     :culture/country "TKM"
     :culture/kind :beverage
     :culture/summary "Fermented camel's milk -- a white, sparkling, sour-flavoured beverage drunk in Turkmenistan."
     :culture/url "https://en.wikipedia.org/wiki/Turkmen_cuisine"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.craft.turkmen-carpet"
     :culture/name "Turkmen carpet"
     :culture/country "TKM"
     :culture/kind :craft
     :culture/summary "Handmade wool floor-covering woven historically by nomadic Turkmen tribes with tribe-specific geometric patterns (Yomut, Ersari, Saryk, Salur, Teke); UNESCO inscribed 'Traditional Turkmen carpet making art in Turkmenistan' on its Representative List of Intangible Cultural Heritage in 2019."
     :culture/url "https://en.wikipedia.org/wiki/Turkmen_carpet"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.festival.turkmen-horse-day"
     :culture/name "Turkmen Horse Day"
     :culture/country "TKM"
     :culture/kind :festival
     :culture/summary "Turkmen state holiday held on the last Sunday of April honouring the Akhal-Teke horse breed; renamed from 'Akhal-Teke Horse Holiday' to 'Turkmen Horse Day.'"
     :culture/url "https://en.wikipedia.org/wiki/Akhal-Teke"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.heritage.merv"
     :culture/name "Merv"
     :culture/country "TKM"
     :culture/kind :heritage
     :culture/summary "Ancient Silk Road city near modern Mary, Turkmenistan; UNESCO listed the site as the World Heritage Site 'State Historical and Cultural Park \"Ancient Merv\"' in 1999."
     :culture/url "https://en.wikipedia.org/wiki/Merv"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}
    {:culture/id "tkm.heritage.nisa"
     :culture/name "Parthian Fortresses of Nisa"
     :culture/country "TKM"
     :culture/kind :heritage
     :culture/summary "Ruins of the fortresses that served as the Parthian Empire's capital for five centuries, in Turkmenistan; declared a UNESCO World Heritage Site in 2007."
     :culture/url "https://en.wikipedia.org/wiki/Parthian_Fortresses_of_Nisa"
     :culture/url-provenance :wikipedia-en
     :culture/retrieved-at "2026-07-17"}]})

(defn spec-basis [iso3] (get catalog iso3))

(defn coverage
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-tkm culture catalog "
                 "(ADR-2607171400 addendum 2, Wave 1): " (count (get catalog "TKM"))
                 " TKM entries, each with a fetched-and-read citation. "
                 "Extend `culture.facts/catalog`, never fabricate an id/url.")})))

(defn by-kind [iso3 kind]
  (filterv #(= (:culture/kind %) kind) (spec-basis iso3)))
