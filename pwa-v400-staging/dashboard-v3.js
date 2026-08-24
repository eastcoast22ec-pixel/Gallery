import "./catalog-v400-preview.js?v=20260824-0341";
import "./rich-history-v10-1.js?v=20260816-2226";
import "./live-summary-index-v4-5.js?v=20260815-0558";
import "./returned-name-hydrator-v4-5.js?v=20260815-0646";
import "./pwa-data-integrity-v4-6.js?v=20260815-1415";
import "./instant-startup-v10-max.js?v=20260816-2118";
import "./pwa-v4-lite.js?v=20260815-0042";
import "./smart-refresh-v3-3.js?v=20260814-2200";
import "./dashboard-v3-base.js";
import "./dashboard-drilldown.js";
import "./ops-v3-1.js";
import "./pwa-hotfix-v3-3.js?v=20260815-0317";
import "./android-v3-2.js";
import "./push-v3-4.js?v=20260814-2125";
import "./operations-v4-3.js?v=20260815-0430";
import "./parcel-rescue-source-time-v4-4.js?v=20260815-0405";
import "./parcel-tracking-bidi-v4-4.js?v=20260815-0445";

function localizeBulkPasteAction() {
  const apply = document.getElementById("bulk-paste-apply");
  if (!apply) return;
  const refresh = () => {
    const summary = document.getElementById("bulk-paste-summary")?.textContent || "";
    const ready = summary.match(/✅\s*(\d+)/)?.[1];
    apply.textContent = ready !== undefined
      ? `تجهيز ${ready} طلبية للإرسال`
      : "تجهيز الطلبيات للإرسال";
  };
  refresh();
  document.getElementById("bulk-paste-analyze")?.addEventListener("click", refresh);
}

localizeBulkPasteAction();

let parcelExplorerPromise = null;
function loadParcelExplorer() {
  if (parcelExplorerPromise) return parcelExplorerPromise;
  if (!document.querySelector('link[data-parcel-explorer-style="1"]')) {
    const explorerStyle = document.createElement("link");
    explorerStyle.rel = "stylesheet";
    explorerStyle.href = "/parcel-explorer.css?v=20260814-1826";
    explorerStyle.dataset.parcelExplorerStyle = "1";
    document.head.append(explorerStyle);
  }
  parcelExplorerPromise = import("./parcel-explorer.js?v=20260815-0042").then((module) => {
    window.setTimeout(() => {
      const active = document.querySelector('[data-explorer-filter="active"]');
      if (active && !document.querySelector("#parcel-explorer-results .parcel-explorer-row")) active.click();
    }, 0);
    return module;
  }).catch((error) => {
    parcelExplorerPromise = null;
    throw error;
  });
  return parcelExplorerPromise;
}

document.querySelector('.nav-button[data-view="tracking"]')?.addEventListener("click", () => {
  loadParcelExplorer().catch(() => {});
});

if (!document.querySelector('link[data-push-v3-4-style="1"]')) {
  const pushStyle = document.createElement("link");
  pushStyle.rel = "stylesheet";
  pushStyle.href = "/push-v3-4.css?v=20260814-2202";
  pushStyle.dataset.pushV34Style = "1";
  document.head.append(pushStyle);
}

const MP_NOTIFICATION_HEADER = { "X-MP-Requested-With": "mp-zr-pwa" };
const NOTIFICATION_LABELS = {
  PENDING: "قيد الانتظار",
  IN_TRANSIT: "قيد النقل",
  OUT_FOR_DELIVERY: "مع الموزع",
  DELIVERED: "تم استلام الطلبية",
  REFUSED: "تم رفض الطلبية",
  RETURNING: "قيد الإرجاع",
  RETURNED: "تم إرجاع الطلبية",
  CANCELED: "تم إلغاء الطلبية",
  PROBLEM: "مشكلة في الطلبية",
  UNKNOWN: "حالة غير معروفة"
};

const notificationTimeFormat = new Intl.DateTimeFormat("fr-DZ", {
  hour: "2-digit",
  minute: "2-digit",
  hour12: false,
  timeZone: "Africa/Algiers"
});

const notificationDateTimeFormat = new Intl.DateTimeFormat("fr-DZ", {
  day: "2-digit",
  month: "2-digit",
  year: "numeric",
  hour: "2-digit",
  minute: "2-digit",
  hour12: false,
  timeZone: "Africa/Algiers"
});

const notificationById = (id) => document.getElementById(id);
const notificationSafe = (value, fallback = "—") => value === null || value === undefined || value === "" ? fallback : String(value);
const notificationLabel = (state) => NOTIFICATION_LABELS[String(state || "UNKNOWN").toUpperCase()] || notificationSafe(state);

function notificationEventTimestamp(item) {
  if (item?.eventTimeTrusted !== true || item?.eventTimeSource !== "zr_event_at") return 0;
  const parsed = Date.parse(item?.eventAt || "");
  return Number.isFinite(parsed) ? parsed : 0;
}

function notificationEventTime(item) {
  const timestamp = notificationEventTimestamp(item);
  return timestamp ? notificationTimeFormat.format(new Date(timestamp)) : "وقت ZR غير موثق";
}

async function fetchNotificationFeed() {
  const response = await fetch("/api/monitor/summary?days=1", {
    headers: MP_NOTIFICATION_HEADER,
    credentials: "same-origin",
    cache: "no-store"
  });
  if (!response.ok) throw new Error(`HTTP ${response.status}`);
  const summary = await response.json();
  return summary?.important_events_today || { items: [] };
}

function openNotificationParcel(reference) {
  if (!reference) return;
  const sheet = notificationById("notification-center");
  if (sheet) sheet.hidden = true;
  document.body.style.overflow = "";
  document.documentElement.style.overflow = "";
  const input = notificationById("tracking-reference");
  if (input) input.value = reference;
  document.querySelector('.nav-button[data-view="tracking"]')?.click();
  notificationById("track-form")?.requestSubmit();
}

function renderTimedNotificationFeed(feed) {
  const list = notificationById("notification-center-list");
  if (!list) return;

  list.replaceChildren();

  if (feed?.integrityBlocked || feed?.status === "integrity_blocked") {
    list.innerHTML = '<div class="list-empty">تم حجب الإشعارات الزمنية مؤقتًا لأن المصدر لم يثبت وقت الحدث من ZR. لن نعرض وقت المزامنة كأنه وقت الاستلام أو الإرجاع.</div>';
    return;
  }

  const items = Array.isArray(feed?.items)
    ? [...feed.items].sort((a, b) => notificationEventTimestamp(b) - notificationEventTimestamp(a))
    : [];

  if (!items.length) {
    const excluded = Number(feed?.excluded_untrusted_time_count || 0);
    list.innerHTML = excluded > 0
      ? `<div class="list-empty">لا توجد أحداث بوقت ZR موثوق حاليًا. تم حجب ${excluded} حدثًا لأن وقت اكتشافه لا يثبت وقت وقوعه.</div>`
      : '<div class="list-empty">لا توجد أحداث مهمة مسجلة اليوم حتى الآن.</div>';
    return;
  }

  for (const item of items) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "notification-row";
    button.dataset.state = String(item.eventNormalizedState || "UNKNOWN").toUpperCase();

    const title = document.createElement("strong");
    title.textContent = notificationSafe(item.customer || item.trackingNumber || item.externalId || item.id, "طلبية");

    const meta = document.createElement("div");
    meta.className = "notification-row-meta";
    const situation = notificationSafe(item.eventSituation, "");
    meta.textContent = [notificationLabel(item.eventNormalizedState), situation, item.trackingNumber, item.externalId]
      .filter(Boolean)
      .join(" · ");

    const time = document.createElement("div");
    time.className = "notification-row-transition";
    time.textContent = `الوقت: ${notificationEventTime(item)}`;
    const timestamp = notificationEventTimestamp(item);
    if (timestamp) time.title = notificationDateTimeFormat.format(new Date(timestamp));

    button.append(title, meta, time);
    button.addEventListener("click", () => openNotificationParcel(item.id || item.trackingNumber));
    list.append(button);
  }
}

async function refreshNotificationFeed() {
  const list = notificationById("notification-center-list");
  const status = notificationById("notification-center-status");
  if (list) list.innerHTML = '<div class="skeleton-block"></div>';
  if (status) status.textContent = "جارٍ التحديث";
  try {
    const feed = await fetchNotificationFeed();
    renderTimedNotificationFeed(feed);
    if (status) {
      status.textContent = feed?.integrityBlocked || feed?.status === "integrity_blocked"
        ? "محجوبة للتحقق"
        : feed?.complete
          ? "مباشر · اليوم"
          : "بيانات موثوقة جزئيًا · اليوم";
    }
  } catch {
    if (status) status.textContent = "بيانات جزئية";
    if (list) list.innerHTML = '<div class="list-empty">تعذر تحديث الإشعارات الآن.</div>';
  }
}

function openNotificationCenter() {
  const sheet = notificationById("notification-center");
  if (!sheet) return;
  sheet.hidden = false;
  document.body.style.overflow = "hidden";
  refreshNotificationFeed();
}

notificationById("important-notifications")?.addEventListener("click", (event) => {
  event.preventDefault();
  event.stopImmediatePropagation();
  openNotificationCenter();
}, true);

notificationById("notification-center-refresh")?.addEventListener("click", (event) => {
  event.preventDefault();
  event.stopImmediatePropagation();
  refreshNotificationFeed();
}, true);

// The legacy secondary detail-close button has an inline handler that CSP can
// block. Keep the markup stable and add a CSP-safe listener instead.
document.querySelector("#parcel-detail .detail-actions .secondary-button")?.addEventListener("click", () => {
  notificationById("detail-close")?.click();
});
