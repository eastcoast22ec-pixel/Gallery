const CATALOG_URL = new URL("./catalog-v400-data.json?v=20260824-0341", import.meta.url).href;
const EXPECTED_WILAYAS = 58;
const EXPECTED_COMMUNES = 1541;

const normalize = (value) => String(value || "")
  .normalize("NFKD")
  .replace(/[\u0300-\u036f]/g, "")
  .toLowerCase()
  .replace(/[^a-z0-9\u0600-\u06ff]+/g, " ")
  .trim()
  .replace(/\s+/g, " ");

const titleCase = (key) => String(key || "")
  .split("_")
  .filter(Boolean)
  .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
  .join(" ");

function injectStyle() {
  if (document.getElementById("catalog-v400-style")) return;
  const style = document.createElement("style");
  style.id = "catalog-v400-style";
  style.textContent = `
    .catalog-v400-banner{margin:0 0 16px;padding:12px 14px;border:1px solid rgba(70,228,194,.28);border-radius:14px;background:rgba(70,228,194,.07);display:flex;gap:8px;align-items:center;justify-content:space-between;flex-wrap:wrap;font-size:13px;line-height:1.55}
    .catalog-v400-banner strong{color:#46e4c2}.catalog-v400-banner span{color:#b9c3c1}
    .catalog-v400-meta{display:block;margin-top:7px;color:#8fa09c;font-size:11px;line-height:1.5}
    select[data-catalog-v400]{width:100%}
    select[data-catalog-v400]:disabled{opacity:.58}
  `;
  document.head.append(style);
}

function banner(message, failed = false) {
  const view = document.getElementById("view-orders");
  if (!view) return;
  let node = document.getElementById("catalog-v400-banner");
  if (!node) {
    node = document.createElement("div");
    node.id = "catalog-v400-banner";
    node.className = "catalog-v400-banner";
    const safety = view.querySelector(".safety-note");
    safety?.insertAdjacentElement("afterend", node);
  }
  node.innerHTML = failed
    ? `<strong>Catalog Preview غير متاح</strong><span>${message}</span>`
    : `<strong>Catalog V400</strong><span>${message}</span>`;
}

function buildCatalog(raw) {
  const counts = raw?.counts || {};
  if (counts.official_wilayas !== EXPECTED_WILAYAS || counts.official_communes !== EXPECTED_COMMUNES) {
    throw new Error(`catalog coverage mismatch (${counts.official_wilayas || 0}/${counts.official_communes || 0})`);
  }
  const wilayaKeys = Array.isArray(raw.official_wilayas) ? [...raw.official_wilayas] : [];
  if (wilayaKeys.length !== EXPECTED_WILAYAS) throw new Error("wilaya list is incomplete");

  const communesByWilaya = new Map(wilayaKeys.map((key) => [key, []]));
  const communeEntries = Object.values(raw.official_communes || {});
  for (const entry of communeEntries) {
    const key = String(entry?.wilaya_key || "");
    const commune = String(entry?.commune || "").trim();
    if (!communesByWilaya.has(key) || !commune) continue;
    communesByWilaya.get(key).push(commune);
  }
  const total = [...communesByWilaya.values()].reduce((sum, items) => sum + items.length, 0);
  if (total !== EXPECTED_COMMUNES) throw new Error(`commune list is incomplete (${total})`);
  for (const items of communesByWilaya.values()) {
    items.sort((a, b) => a.localeCompare(b, "fr", { sensitivity: "base" }));
  }
  return { wilayaKeys, communesByWilaya, counts, source: raw.source || {} };
}

function createSelect(input, kind) {
  const select = document.createElement("select");
  select.name = input.name;
  select.required = input.required;
  select.disabled = input.disabled;
  select.dataset.catalogV400 = kind;
  select.setAttribute("autocomplete", input.getAttribute("autocomplete") || "off");
  if (input.getAttribute("aria-label")) select.setAttribute("aria-label", input.getAttribute("aria-label"));
  return select;
}

function addOption(select, value, label, selected = false, disabled = false) {
  const option = document.createElement("option");
  option.value = value;
  option.textContent = label;
  option.selected = selected;
  option.disabled = disabled;
  select.append(option);
  return option;
}

function findWilayaKey(catalog, rawWilaya, rawCommune) {
  const wanted = normalize(rawWilaya);
  if (wanted) {
    const byName = catalog.wilayaKeys.find((key) => normalize(key) === wanted || normalize(titleCase(key)) === wanted);
    if (byName) return byName;
  }
  const commune = normalize(rawCommune);
  if (!commune) return "";
  const matches = catalog.wilayaKeys.filter((key) => catalog.communesByWilaya.get(key).some((name) => normalize(name) === commune));
  return matches.length === 1 ? matches[0] : "";
}

function fillCommunes(select, catalog, wilayaKey, preferred = "") {
  select.replaceChildren();
  addOption(select, "", wilayaKey ? "اختر البلدية" : "اختر الولاية أولاً", !preferred, true);
  const items = catalog.communesByWilaya.get(wilayaKey) || [];
  const wanted = normalize(preferred);
  let matched = false;
  for (const commune of items) {
    const selected = wanted && normalize(commune) === wanted;
    matched ||= Boolean(selected);
    addOption(select, commune, commune, selected);
  }
  select.disabled = !wilayaKey;
  if (preferred && !matched) {
    addOption(select, preferred, `${preferred} · محفوظة`, true);
  }
}

function enhanceCard(card, catalog) {
  if (!card || card.dataset.catalogV400Ready === "1") return;
  const wilayaInput = card.querySelector('[name="wilaya"]');
  const communeInput = card.querySelector('[name="commune"]');
  if (!wilayaInput || !communeInput) return;

  const currentWilaya = wilayaInput.value || "";
  const currentCommune = communeInput.value || "";
  const selectedKey = findWilayaKey(catalog, currentWilaya, currentCommune);
  const wilayaSelect = createSelect(wilayaInput, "wilaya");
  addOption(wilayaSelect, "", "اختر الولاية", !selectedKey, true);
  for (const key of catalog.wilayaKeys) {
    const option = addOption(wilayaSelect, titleCase(key), titleCase(key), key === selectedKey);
    option.dataset.wilayaKey = key;
  }
  if (currentWilaya && !selectedKey) {
    const option = addOption(wilayaSelect, currentWilaya, `${currentWilaya} · محفوظة`, true);
    option.dataset.wilayaKey = "";
  }

  const communeSelect = createSelect(communeInput, "commune");
  fillCommunes(communeSelect, catalog, selectedKey, currentCommune);

  wilayaInput.replaceWith(wilayaSelect);
  communeInput.replaceWith(communeSelect);
  const homeFields = card.querySelector(".home-fields");
  if (homeFields && !homeFields.querySelector(".catalog-v400-meta")) {
    const note = document.createElement("small");
    note.className = "catalog-v400-meta";
    note.textContent = "اختر الولاية ثم البلدية من Catalog الرسمي المضمّن في نسخة الفحص.";
    homeFields.append(note);
  }

  wilayaSelect.addEventListener("change", () => {
    const key = wilayaSelect.selectedOptions[0]?.dataset?.wilayaKey || "";
    fillCommunes(communeSelect, catalog, key, "");
    communeSelect.dispatchEvent(new Event("input", { bubbles: true }));
  });
  card.dataset.catalogV400Ready = "1";
}

async function start() {
  injectStyle();
  try {
    const response = await fetch(CATALOG_URL, { cache: "no-store", credentials: "same-origin" });
    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const raw = await response.json();
    const catalog = buildCatalog(raw);
    banner(`${catalog.counts.official_wilayas} ولاية · ${catalog.counts.official_communes} بلدية · Snapshot ${catalog.source.authority_as_of || "موثّق"}`);
    const list = document.getElementById("orders-list");
    if (!list) return;
    list.querySelectorAll(".order-card").forEach((card) => enhanceCard(card, catalog));
    const observer = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        for (const node of mutation.addedNodes) {
          if (!(node instanceof Element)) continue;
          if (node.matches?.(".order-card")) enhanceCard(node, catalog);
          node.querySelectorAll?.(".order-card").forEach((card) => enhanceCard(card, catalog));
        }
      }
    });
    observer.observe(list, { childList: true, subtree: true });
  } catch (error) {
    console.error("Catalog V400 preview failed", error);
    banner("تم إبقاء حقول النص الأصلية دون تعديل لأن تغطية الكاتالوغ لم تُثبت.", true);
  }
}

start();
