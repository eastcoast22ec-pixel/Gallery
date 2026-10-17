<!-- CURRENT-RECONCILIATION-2026-10-17-GALLERY -->
# PROJECT_STATE — Gallery

Canonical continuity record for `eastcoast22ec-pixel/Gallery`.

Last forensic reconciliation: **2026-10-17T12:50+01:00**.

## READ THIS FIRST

**STATUS: ACTIVE DEFECT / RCA CLOSED / FIX PENDING / NO PRODUCT-CODE FIX WRITTEN YET**

The previous continuity state (`STABLE / DORMANT / NO ACTIVE BACKLOG`) is superseded. The owner supplied fresh runtime regression evidence on 2026-10-17 and explicitly reopened Gallery. The defect is the date-section selection regression where media become selected while the contextual action bar (CAB, including delete/trash) is absent.

For this specific observed defect and installed source revision:

- **RCA: CLOSED**
- **RCI: COMPLETE**
- **UNKNOWN WITHIN THIS RCA SCOPE: 0**
- **FIX: NOT IMPLEMENTED YET**
- **FIX VALIDATION: NOT STARTED**

Do not confuse a closed RCA with a validated fix. No source correction, build, deploy, or final Android acceptance has been performed in this continuity-only reconciliation.

## REPOSITORY TRUTH AT REOPEN

Repository: `eastcoast22ec-pixel/Gallery`

Stable branch: `main`

Pre-continuity-write `main` HEAD verified immediately before this file update:

`63caa30807fa58c75616d37dd410ef76f5202034`

That commit is continuity-only (`[skip ci] state(gallery): reconcile stable dormant continuity`). There were **no open pull requests** at the 2026-10-17 reconciliation point.

The last Product-code merge remains:

`93023f08bf5bbd949a6481a1a4054568b0f70f9d`

`93023f...` is no longer to be described as a defect-free "accepted" product baseline. It is the current Product-code baseline that contains the confirmed date-section regression.

Post-product commits before this write were state/policy-only:

- `4d27dc78ec7ec7813447b33c55b4602e98e59f66` — dormant state
- `5fa056108234d8ec7b4136bb9992bc3bf0ad6828` — repository guards
- `74a5e153993948c99f6331b9cf73d2760b086a6d` — human-only Android acceptance policy
- `63caa30807fa58c75616d37dd410ef76f5202034` — continuity reconciliation

Historical branches are non-authoritative unless explicitly reopened. No historical branch creates backlog by itself.

## REPOSITORY EXECUTION RULES THAT REMAIN BINDING

Read `AGENTS.md` before any consequential write.

Important active constraints:

- live GitHub/runtime/user evidence outranks historical continuity;
- inspect fresh `main` and parallel work before code writes;
- preserve recovery evidence;
- do not build/deploy merely to refresh continuity;
- final Android acceptance is **HUMAN-ONLY**; automated build/static/unit checks can support a fix but cannot issue final Android acceptance PASS/FAIL.

## USER RUNTIME EVIDENCE

The owner reported the affected phone build as:

`1.13.1-nightly-3751736`

Source revision matched during the investigation:

`3751736e5b87091131995021a57c19c49bb5f5c4`

This runtime/build string is user-supplied evidence. Git proves the referenced commit and its source; the phone-install fact itself comes from the owner's runtime evidence.

### Supplied regression video evidence

Conversation attachment used for the forensic audit:

`1000038169.mp4`

Locally verified evidence fingerprint:

- SHA-256: `40fcd4737334273bde3c7b17e052794dcfd0d51a483f0391258047eb758d561f`
- bytes: `31,402,584`
- dimensions: `720 x 1584`
- frames: `1,981`
- duration: `32.346089 s`
- average frame rate: `44572500/727787` (~61.244 fps)

The binary video is conversation evidence and is **not copied into Git** by this continuity write. The fingerprint above exists so a later handoff can verify any re-supplied copy is the same evidence.

### Forensically observed sequence in that video

The following is observation from the supplied video, not a guess:

1. CAB is visible with the target date section at `53/53`.
2. CAB disappears while the normal top/search toolbar returns.
3. Immediately before the decisive dataset reflow, an empty `Hier` section header is visible immediately before `15/10/2026`.
4. The empty `Hier` section then disappears and list positions shift.
5. Shortly after the shift, the target section becomes `2/53` **without CAB**.
6. A subsequent section-control interaction produces `53/53` **still without CAB**.
7. Deselecting returns the section to zero selection while CAB remains absent.
8. A later fresh selection from zero causes CAB to return and shows `53/53`.

Frame inspection around ~10.9–11.1 s shows the transition into `2/53`; the broader ~11–17 s timeline shows `2/53 -> 53/53 without CAB -> zero without CAB -> later fresh CAB return`.

## INSTALLED REVISION == CURRENT MAIN FOR THE RELEVANT PRODUCT PATH

The defect is not already fixed on current `main`.

Relevant blobs were independently checked at the installed revision and current pre-write `main`; they are identical:

- `app/src/main/kotlin/org/fossify/gallery/views/SectionSelectButton.kt`
  - blob SHA: `075c45a9100b053a709a89a4441446c8196f7dbe`
- `app/src/main/kotlin/org/fossify/gallery/views/DateSectionSelectionController.kt`
  - blob SHA: `f8ae74264323737023750f1fa14e1414bc18db05`
- `app/src/main/kotlin/org/fossify/gallery/adapters/MediaAdapter.kt`
  - blob SHA: `9847b72e6469a877262844c71e6b6e3f3e1d10a3`
- `app/src/main/kotlin/org/fossify/gallery/activities/MediaActivity.kt`
  - blob SHA: `dbfca54033ab7fa9bb235eab9a9b51ae338ad7d3`
- `gradle/libs.versions.toml`
  - blob SHA: `5c45171dbf74063eb0536bad9e4779a3c875c59e`

Therefore the causal Product code investigated at `3751736...` remains present in the current Product baseline on `main`.

## DEPENDENCY PIN USED BY THE RCA

Gallery pins Fossify Commons:

`commons = "6.1.6"`

Annotated tag object:

`e21cfd85c0205a09f9bd485e2220deb7ab3fba08`

Dereferenced Fossify Commons source commit:

`cbee1f1385a006f9f82c1a2c8b3722b5f0a242bd`

Relevant upstream blobs at that exact commit:

- `commons/src/main/kotlin/org/fossify/commons/views/MyRecyclerView.kt`
  - blob SHA: `e726b0c5bfa5cc9785f2d6195d634a18e75d6bbd`
- `commons/src/main/kotlin/org/fossify/commons/adapters/MyRecyclerViewAdapter.kt`
  - blob SHA: `ded508f5b5ad166cef703e4e1c63a0e1b0aaaa23`

These exact pinned sources, not a guessed newer Commons implementation, are authoritative for the RCA.

## FEATURE ORIGIN / REGRESSION HISTORY

### Original section-selection feature

PR #1 / merge commit:

`0cdae2e5588cb98d4cc07c0bac50df3db55bd234`

The implementation explicitly chose to reuse the existing contextual range-selection mechanism. It programmatically called a media item's `performLongClick()` and then reused range-selection behavior. This was the architectural origin of programmatic UI selection being routed through a human long-press/drag gesture state machine.

### Toggle/cumulative extension

Commit:

`555e024d83cb6d23cb2e22197b9245fe380a75c8`

This introduced cumulative section selection and the key invalid proxy:

`selectedKeys.isNotEmpty()` => treat selection mode as already established.

It also introduced reflection into protected/private Commons selection internals.

### Reflection hardening

Commit:

`bbbbbf158952fda063b68793bb722774abd1c8f2`

Reflection calls were wrapped in `runCatching`; failure can return early without surfacing a crash. This remains technical debt and a secondary correctness risk, but it is **not required to explain the observed `2/53` regression** and must not be promoted to the primary RCA without new evidence.

### State extraction/refactor

- `15e544735c9ecc9fed7f20257ad6cf59e8f98376` — isolate date-section selection state
- `3751736e5b87091131995021a57c19c49bb5f5c4` — slim selector view
- `93023f08bf5bbd949a6481a1a4054568b0f70f9d` — merge date-section selection improvements into `main`

The refactor did not remove the unsafe `performLongClick()` bridge or the `hasSelection()` proxy.

## SOURCE FACTS — GALLERY

### `SectionSelectButton.kt`

When the section is not fully selected:

- if `DateSectionSelectionController.hasSelection(adapter)` is true, it applies selection directly and **does not start ActionMode**;
- otherwise it resolves the first media holder and calls:

`firstHolder.itemView.performLongClick()`

Then, if selection exists, it applies the rest of the section programmatically.

### `DateSectionSelectionController.kt`

`hasSelection(adapter)` is exactly based on whether reflected `selectedKeys` is non-empty. It does **not** verify `ActionMode`, `actModeCallback.isSelectable`, CAB visibility, or lifecycle ownership.

`applySelection(...)` reflectively invokes the inherited protected `toggleItemSelection(Boolean, Int, Boolean)` and private `updateTitle()`.

Therefore the section control can mutate `selectedKeys` in a state where ActionMode is absent.

### `MediaAdapter.kt`

`updateMedia(newMedia)` does this when the media-list hash changes:

1. replaces `media` with the new list;
2. calls `notifyDataSetChanged()`;
3. calls `finishActMode()`.

Selection keys are path hashes and adapter positions are recomputed against the current `media` list.

### `MediaActivity.kt`

Media loading/refresh has real asynchronous dataset-replacement paths:

- initial load can emit cached media first and then a live `GetMediaAsynctask` result;
- periodic latest-media checks can trigger another `getMedia()`;
- `setupAdapter()` sends new `mMedia` to `MediaAdapter.updateMedia(...)` when an adapter already exists.

Thus a live dataset replacement while the user is in selection mode is an intended/normal runtime possibility, not a hypothetical impossible interleaving.

## SOURCE FACTS — FOSSIFY COMMONS 6.1.6

### ActionMode lifecycle is separate from selection data

`MyRecyclerViewAdapter` owns, separately:

- `selectedKeys`
- `actMode`
- `actModeCallback.isSelectable`
- `lastLongPressedItem`

`onCreateActionMode(...)` clears `selectedKeys`, sets selectable state, and assigns `actMode`.

`onDestroyActionMode(...)`:

- sets selectable false;
- deselects mapped selected keys;
- clears `selectedKeys`;
- clears the action-bar title;
- sets `actMode = null`;
- resets `lastLongPressedItem = -1`.

This proves that the CAB disappearance caused by a completed ActionMode teardown does **not** simply leave the old selected keys untouched.

### `toggleItemSelection(...)` does not require ActionMode

The inherited method directly adds/removes `selectedKeys`, notifies the adapter, optionally updates the title, and only calls `finishActMode()` if the set becomes empty. It does not assert that `actMode != null` or `isSelectable == true` before adding selection.

Therefore selection data can exist without CAB if some path calls this method outside a live ActionMode.

### Synthetic long click arms drag selection

`ViewHolder.viewLongClicked()`:

1. starts ActionMode if the callback is not selectable;
2. selects the current item;
3. calls `itemLongClicked(currentPosition)`.

`itemLongClicked(...)` calls:

`recyclerView.setDragSelectActive(position)`

`MyRecyclerView.setDragSelectActive(...)` stores `initialSelection`, sets `dragSelectActive = true`, and invokes the drag listener's `selectItem(initialSelection)`.

### Drag state is owned by `MyRecyclerView`, not ActionMode

`MyRecyclerView` separately owns:

- `dragSelectActive`
- `initialSelection`
- `lastDraggedIndex`
- `minReached`
- `maxReached`

Its ACTION_UP/ACTION_CANCEL branch resets drag state. Its ACTION_MOVE branch, when drag is active, computes the current adapter item position and calls the drag listener's `selectRange(...)`.

`MyRecyclerViewAdapter.onDestroyActionMode(...)` does not reset `MyRecyclerView.dragSelectActive` and has no call that cancels that drag state.

## ANDROID TOUCH-DISPATCH FACT THAT CLOSES THE LAST RCA GAP

For an ordinary clickable Android `View`, the ACTION_UP path posts a `PerformClick` runnable instead of necessarily calling `performClick()` inline. Android's own comment states that the runnable is used rather than calling `performClick` directly so view state can update before click actions start.

Pinned public AOSP reference used during this RCA:

`https://android.googlesource.com/platform/frameworks/base/+/android-15.0.0_r1/core/java/android/view/View.java`

Android 15 source tree reference:

`https://android.googlesource.com/platform/frameworks/base/+/android-15.0.0_r1/core/java/android/view/`

This ordering matters because the section button's click callback can run **after the physical ACTION_UP has already traversed the RecyclerView**. The callback then synthetically calls `performLongClick()` on a media item, which arms `dragSelectActive`; there is no second physical ACTION_UP generated by that synthetic long-click call to reset the newly armed RecyclerView drag state.

This is the missing lifecycle link that makes the ghost-drag mechanism deterministic rather than speculative.

## FINAL ROOT-CAUSE CHAIN FOR THE OBSERVED DEFECT

This causal chain is the canonical RCA. Do not replace it with older hypotheses unless new contradictory evidence is produced.

1. User taps the date-section selector.
2. Android completes the physical tap's ACTION_UP path and posts the click action.
3. The section button's posted click callback runs and calls `performLongClick()` on the first media holder.
4. Commons `viewLongClicked()` starts ActionMode, selects the item, then calls `itemLongClicked()`.
5. `itemLongClicked()` arms `MyRecyclerView.dragSelectActive` with the current numeric `initialSelection`.
6. Because that drag state was armed from the posted click after the physical ACTION_UP, the physical ACTION_UP that would normally clear drag state has already occurred; the synthetic `performLongClick()` does not generate a new touch UP event.
7. Section selection is expanded to the full date; CAB is initially visible.
8. A real media dataset replacement occurs. In the observed video the empty `Hier` header disappears, proving a list reflow/position shift.
9. `MediaAdapter.updateMedia()` replaces the list, notifies the RecyclerView, and finishes ActionMode.
10. ActionMode teardown clears adapter selection state and `lastLongPressedItem`, but it does **not** clear the separately-owned `MyRecyclerView.dragSelectActive` state or its old numeric `initialSelection`.
11. The next physical interaction arrives while the RecyclerView still believes drag selection is active. An ACTION_MOVE is interpreted through `selectRange(...)` against the **new adapter positions**.
12. In the observed layout, the stale starting position and the shifted header/media positions produce selection of the first two selectable media items; the section UI becomes exactly `2/53` while ActionMode/CAB is absent.
13. That actual touch eventually supplies ACTION_UP and clears the ghost drag state, but the two selected keys created by the drag listener remain.
14. On the next section-button tap, `DateSectionSelectionController.hasSelection()` sees non-empty `selectedKeys` and incorrectly treats that as sufficient selection-mode state. It bypasses `performLongClick()`/ActionMode creation and directly selects the rest of the section.
15. The UI becomes `53/53` while CAB is still absent — exactly the observed invariant violation.
16. Deselecting can return the set to empty while `actMode` is already null; `finishActMode()` is then a no-op (`actMode?.finish()`).
17. With selection empty and the ghost drag already cleared by a real ACTION_UP, a later fresh section tap again takes the `performLongClick()` path and creates a new ActionMode, so CAB returns. This matches the later video state.

## THE TWO BROKEN INVARIANTS

The defect is architectural, not a one-line rendering bug.

Broken invariant A:

`selectedKeys.isNotEmpty()` is treated as equivalent to "selection ActionMode is active".

That equivalence is false in Commons because selection storage and ActionMode lifecycle are separate states.

Broken invariant B:

A programmatic "select date section" operation is implemented by synthesizing a human long-press gesture. That gesture activates the drag-selection state machine, which has its own lifecycle independent of ActionMode and dataset refresh.

The combination of these two broken invariants explains the complete observed failure sequence.

## EXCLUDED / SUPERSEDED HYPOTHESES

The following were investigated and are **not** the primary RCA for the observed video:

### "`finishActMode()` leaves the old selectedKeys behind"

False. Commons `onDestroyActionMode(...)` explicitly clears `selectedKeys` and resets `lastLongPressedItem`.

### "stale `lastLongPressedItem` survives CAB teardown"

False for a completed ActionMode teardown. It is reset to `-1`.

### "reflection fails after exactly two items and that creates `2/53`"

Not needed to explain the observed `2/53`, and no runtime exception evidence identified an exact reflection failure at item 2. Reflection remains brittle secondary debt, but this theory is superseded as the primary explanation.

### "drag state always gets cleared by the tap's ACTION_UP, so it cannot survive"

Incorrect because Android posts the button click action from the ACTION_UP path. The section click can arm drag state only after the physical ACTION_UP has already been processed by the RecyclerView.

## FIX STATUS AND NON-NEGOTIABLE CORRECTION INVARIANTS

No fix has been written yet.

Next work begins at **root-level fix design/implementation**, not at RCA discovery.

The correction must be general and must not be a timing patch around the observed 53-item section.

Required invariants for the correction:

1. Programmatic date-section selection must **not** enter selection mode via `performLongClick()` or any synthetic human gesture.
2. Programmatic section selection must **not** arm drag-selection state.
3. Selection lifecycle must have an explicit authoritative state; non-empty `selectedKeys` alone must never be used as proof that ActionMode/CAB exists.
4. It must be impossible for `selectedKeys` to become non-empty through the section-selection API while ActionMode is absent, unless the design explicitly defines a non-CAB selection mode and all callers support it. The current app does not define such a mode.
5. Dataset refresh must leave the system in one coherent state: either a deliberately preserved valid selection + valid ActionMode, or a fully cleared selection + fully cleared interaction state. No split state is allowed.
6. Preserve intended product behavior: date-section toggle, cumulative multi-day selection, partial/checked state counts, normal manual selection, and single-pick external intent behavior.
7. Remove or strongly contain reflection into Commons selection internals; prefer explicit adapter-owned APIs and state transitions.
8. Do not alter unrelated Gallery flows.
9. Final Android acceptance is human-only per `AGENTS.md`.

Do **not** claim any of these fix invariants PASS until code exists and is actually verified.

## THREE CLEAN FORENSIC ROUNDS — 2026-10-17

### ROUND 1 — repository/current-state reconciliation — PASS

Independently verified:

- fresh `main` HEAD before write = `63caa30807fa58c75616d37dd410ef76f5202034`;
- canonical `AGENTS.md` rules read before write;
- old `.chatgpt/PROJECT_STATE.md` read in full;
- old continuity was materially stale because it still said dormant/no backlog after fresh owner regression evidence;
- no open PRs;
- relevant current-main Product blobs match the investigated installed source revision;
- no Product fix was found on `main`.

Drift corrected by this continuity rewrite: status, current action, product-baseline interpretation, RCA, evidence, next action, and acceptance boundary.

### ROUND 2 — history/dependency/source reconciliation — PASS

Independently verified:

- feature origin PR #1 and its explicit reuse of contextual range-selection;
- cumulative/toggle change commit `555e024d...`;
- reflection-hardening commit `bbbbbf...`;
- installed/source ref `3751736...`;
- merge `93023f...`;
- exact Commons version `6.1.6`;
- annotated Commons tag and dereferenced source commit;
- exact upstream Commons files governing ActionMode, selection storage, and drag state.

No alternative branch or newer dependency implementation was substituted into the RCA.

### ROUND 3 — runtime/source causal closure — PASS

Independently verified:

- video fingerprint and media metadata;
- frame-level observed state sequence;
- empty-header disappearance/list reflow before `2/53`;
- `2/53` and later `53/53` occur without CAB;
- later fresh zero-to-selected path recreates CAB;
- Gallery synthetic `performLongClick()` path;
- Commons long-click -> `itemLongClicked()` -> `setDragSelectActive()` path;
- Commons ActionMode teardown does not reset RecyclerView drag state;
- Android View ACTION_UP click dispatch posts `PerformClick`, closing the timing/lifecycle gap;
- subsequent stale-position drag range + `hasSelection()` proxy accounts for the complete observed sequence.

Result: **RCA 100% / RCI complete / 0 unknown within this specific defect scope**.

Again: this certification applies to causal diagnosis only. It does **not** certify a fix, because no fix exists yet.

## CURRENT ACTION / NEW-CHAT HANDOFF

A new conversation must **not start from zero** and must **not reopen the RCA unless new evidence contradicts it**.

Start in this order:

1. Read `AGENTS.md`.
2. Read this entire `.chatgpt/PROJECT_STATE.md`.
3. Fetch fresh `main` and confirm whether anything changed after the continuity-only commit containing this file.
4. Confirm relevant Product blobs before editing.
5. Continue directly with root-level/general/final correction design and implementation for the date-section selection state machine.
6. Preserve rollback/recovery evidence and document every decisive result back into this file.
7. Do not claim final Android acceptance; reserve that for human verification on device.

### Current technical stopping point

**RCA is finished. The next technical task is to implement the correction.**

There is no need to rediscover why CAB disappears or why `2/53` and then `53/53` can exist without CAB. The canonical explanation is the ghost drag state created by synthetic long-press selection, surviving ActionMode teardown/list reflow, followed by the invalid `selectedKeys != empty` selection-mode proxy.

## CONTINUITY WRITE SCOPE

This 2026-10-17 operation is documentation/state only.

It is not authorization evidence for an already-completed Product fix, because no Product fix was performed here.

No Build, Deploy, emulator/UI acceptance, release, or provider mutation was required for this continuity save.

The only intended Git content change from pre-write `main@63caa30807fa58c75616d37dd410ef76f5202034` is this canonical `.chatgpt/PROJECT_STATE.md` update.
