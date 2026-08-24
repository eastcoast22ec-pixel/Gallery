# V400 PWA Staging Handoff — 2026-08-24 17:19 +01

## Mandatory first action
Fetch current HEAD/state first → reconcile → inspect parallel work → continue. Do not trust this checkpoint over live GitHub/provider state.

## Objective
Deliver a full Android-inspectable HTTPS staging PWA for V400 catalog acceptance before any Production promotion.

## Primary repository
- Repository: `eastcoast22ec-pixel/mirage-pub-cloud-runner`
- Production source branch: `mp-export-zr-connector`
- Fresh verified Production-source HEAD at checkpoint: `e9c69ebc31280142be4244163e5224e20af662f8`
- Latest commit message: `fix(cloudflare): restore last-known-good native build gate`
- Do not infer live runtime deployment identity from the source HEAD alone.

## Candidate
- Branch: `v400-cat-preview`
- Exact candidate HEAD: `1fef8f5612b16b070437a2adeeb1af53aad6a2ab`
- Functional parent: `8432cb65c60f3c9f5defaca47db8681bc1addbfa`
- PR #104: OPEN, DRAFT, NOT MERGED, DO NOT MERGE until user explicitly accepts staging and separately authorizes promotion.
- Scope: V400 catalog selector/PWA assets; expected coverage 58 wilayas / 1541 communes.

## Public staging mirror
- Repository: `eastcoast22ec-pixel/Gallery`
- Runtime staging branch: `pwa-catalog-v400-preview-20260824`
- Fresh verified staging HEAD before checkpoint branch creation: `2cfac5285937c907fee7c485cd608f79c0d424a9`
- Commit message: `stage full V400 PWA read-only inspection shell`
- Relevant directory: `pwa-v400-staging/`
- `index.html` at this HEAD is a standalone dark read-only catalog inspection shell and references `catalog-v400-preview.js`.
- `catalog-v400-preview.js` uses a pinned public transport dataset commit `0c7591fa6fa0e42fa1fcb22afc0aeeadec36bf13`, verifies exactly 58 wilayas / 1541 communes, and fails closed on mismatch.

## User evidence
- The earlier `raw.githack.com` staging URL opened to a black/blank page on Android. Treat that host/path as failed user acceptance evidence.
- A later staging fix removed the blank runtime loader; then parallel work advanced staging to `2cfac528...`.
- The latest `2cfac528...` shell has NOT yet been user-verified on Android in this conversation.

## Hosting findings
- Cloudflare Preview alias path was not reliably provider-verifiable from this ChatGPT Android session.
- Replit first-time publish path requires active subscription; reject under 0-DZD policy.
- Vercel account is connected on Hobby, but earlier deployment/readback/team-permission behavior was unreliable; no verified live V400 staging URL was obtained from Vercel.
- `raw.githack.com` was user-observed blank and should not be reused blindly.
- Consider a different host such as a fixed-commit CDN/GitHub-native path only after actual verification; do not claim success without Android-visible or provider evidence.

## Safety / invariants
- Android-only user workflow.
- 0 DZD / free-tier-first.
- No GitHub Actions.
- No unnecessary CircleCI build/rerun.
- No new paid service or card requirement.
- No Cloudflare Production promotion for PR #104 before explicit acceptance/authorization.
- No backend/Render mutation merely to inspect the frontend catalog.
- Staging must remain read-only: no POST/PUT/PATCH/DELETE to real backend during inspection.
- Preserve candidate exact SHA unless a verified bug requires a candidate patch; if HEAD moves, refetch and reconcile.

## Parallel work warning
Both the Production-source branch and Gallery staging branch moved during the previous chat. Re-fetch them before every consequential write and preserve newer work.

## Next action
1. Re-fetch Production source, candidate, PR #104, and Gallery staging HEAD.
2. Inspect delta since `2cfac528...` if staging moved.
3. Obtain a different free HTTPS serving path for the existing staging shell and verify actual HTTP/render behavior.
4. Send user only a link that is actually verified or clearly label it unverified.
5. User performs Android visual acceptance.
6. Only after acceptance, reconcile PR #104 against current Production source and request/confirm separate Production promotion authorization.

## Recovery
- Runtime staging baseline before this checkpoint: `2cfac5285937c907fee7c485cd608f79c0d424a9`.
- Previous standalone-shell checkpoint: `75400611116807504b4d417fa2f8377232fd354e`.
- Candidate baseline: `1fef8f5612b16b070437a2adeeb1af53aad6a2ab`.
- Production source at checkpoint: `e9c69ebc31280142be4244163e5224e20af662f8`.

## Notes
- Root `AGENTS.md` and `.chatgpt/PROJECT_STATE.md` were not found on `mirage-pub-cloud-runner/main` during this checkpoint pass.
- This checkpoint is intentionally stored on a separate branch so it does not move the runtime staging branch.
