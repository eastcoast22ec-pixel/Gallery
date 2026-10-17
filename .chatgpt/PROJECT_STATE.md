<!-- CURRENT-RECONCILIATION-2026-10-17-GALLERY-ROOT-FIX-MERGED -->
# PROJECT_STATE — Gallery

Canonical continuity record for `eastcoast22ec-pixel/Gallery`.

Last reconciliation: **2026-10-17 after PR #4 merge**.

## READ THIS FIRST

**STATUS: ROOT-LEVEL FIX MERGED / AUTOMATED VALIDATION PASS / FINAL ANDROID ACCEPTANCE PENDING HUMAN**

For the confirmed date-section selection / missing-CAB regression:

- **RCA: CLOSED — 100% within the certified defect scope**
- **RCI: COMPLETE**
- **UNKNOWN WITHIN THIS RCA SCOPE: 0**
- **ROOT-LEVEL FIX: IMPLEMENTED AND MERGED TO `main`**
- **AUTOMATED VALIDATION: PASS**
- **FINAL ANDROID ACCEPTANCE: PENDING — HUMAN ONLY**

Do **not** reopen the RCA unless new contradictory runtime evidence appears.
Do **not** claim final Android PASS until a human verifies the corrected Android build on-device, per `AGENTS.md`.

## CURRENT REPOSITORY TRUTH

Repository: `eastcoast22ec-pixel/Gallery`

Stable branch: `main`

Product-code merge containing the root fix:

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

Merge title:

`Merge pull request #4 from eastcoast22ec-pixel/fix/gallery-date-section-selection-state-20261017`

PR:

`#4 — fix: make date-section selection lifecycle coherent`

Validated PR head:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Pre-fix continuity HEAD / recovery target:

`bf08518d5b2902cc9849f156efe25397e4392c09`

Important recovery note: `bf08518...` is a technically valid rollback target if the new correction introduces a new regression, but it contains the already-confirmed original CAB/date-section defect. It must **not** be described as defect-free product LKG.

The new root-fix merge must also **not** be promoted to final Android LAST_KNOWN_GOOD until human device acceptance is completed.

## FULL FORENSIC RCA ARCHIVE

The complete certified forensic RCA that preceded this correction remains immutably preserved in Git at:

`bf08518d5b2902cc9849f156efe25397e4392c09:.chatgpt/PROJECT_STATE.md`

That archive contains the full video evidence chain, exact source/dependency facts, rejected hypotheses, Android touch-dispatch closure, and the three independent clean forensic rounds. This current file is the canonical forward state; use the archived revision only when full forensic detail is required.

## USER RUNTIME EVIDENCE RETAINED

Affected owner-reported build:

`1.13.1-nightly-3751736`

Investigated source revision:

`3751736e5b87091131995021a57c19c49bb5f5c4`

Regression video:

`1000038169.mp4`

Verified video fingerprint:

- SHA-256: `40fcd4737334273bde3c7b17e052794dcfd0d51a483f0391258047eb758d561f`
- bytes: `31,402,584`
- dimensions: `720 x 1584`
- frames: `1,981`
- duration: `32.346089 s`

Observed decisive sequence:

`53/53 with CAB -> dataset/list reflow -> 2/53 without CAB -> 53/53 without CAB -> zero -> later fresh selection recreates CAB`

## CERTIFIED ROOT CAUSE — COMPACT CANONICAL FORM

The observed failure required two broken invariants acting together:

### Root cause A — invalid selection-mode proxy

The date-section feature treated:

`selectedKeys.isNotEmpty()`

as equivalent to:

`selection ActionMode / CAB is active`

That equivalence is false in Fossify Commons because selected-key storage and ActionMode lifecycle are separate state.

### Root cause B — programmatic selection entered through a synthetic human gesture

Programmatic date-section selection called:

`performLongClick()`

on a media holder. Commons then executed:

`viewLongClicked() -> itemLongClicked() -> recyclerView.setDragSelectActive(position)`

Because Android posts the button click action after the physical tap ACTION_UP path, the synthetic long-click could arm RecyclerView drag-selection state after the physical ACTION_UP had already occurred. No matching new physical ACTION_UP existed to clear that newly armed drag state.

A subsequent real dataset replacement/list reflow finished ActionMode but did not reset the separately owned RecyclerView drag state. The next touch could therefore apply a stale numeric range against new adapter positions, producing the observed `2/53` without CAB. The old `hasSelection()` proxy then allowed the next section action to expand that detached selection to `53/53` without recreating ActionMode.

## DEPENDENCY FACTS RETAINED

Gallery pin used by the RCA:

`Fossify Commons 6.1.6`

Exact upstream source commit:

`cbee1f1385a006f9f82c1a2c8b3722b5f0a242bd`

Authoritative upstream facts:

- `selectedKeys`, `actMode`, `actModeCallback.isSelectable`, and RecyclerView drag state are distinct state.
- `toggleItemSelection(...)` can mutate selected keys without requiring live ActionMode.
- `viewLongClicked()` enters ActionMode and then arms drag selection.
- RecyclerView ACTION_UP/ACTION_CANCEL clears its drag state.
- Commons ActionMode teardown does not itself reset RecyclerView drag state.

## ROOT-LEVEL CORRECTION IMPLEMENTED

This is **not** a timing workaround and is not special-cased for 53 items.

The correction changes ownership and lifecycle invariants:

1. Programmatic date-section selection no longer uses `performLongClick()`.
2. The section UI no longer searches/scrolls for a ViewHolder or retries layout merely to synthesize a human gesture.
3. `DateSectionSelectionController` no longer uses reflection to access `selectedKeys`, `toggleItemSelection`, or `updateTitle`.
4. `MediaAdapter` is now the explicit owner of programmatic section-selection state transitions because it already owns the inherited Commons ActionMode and selected-key state.
5. A section selection is considered valid only when the authoritative selection lifecycle is live:

`actMode != null && actModeCallback.isSelectable`

6. Programmatic selection starts ActionMode directly through its real callback, then mutates selection through the inherited protected selection API.
7. Detached/split selection state is defensively normalized instead of being treated as a valid CAB session.
8. On ActionMode destruction, RecyclerView selection-gesture state is cancelled through its normal ACTION_CANCEL lifecycle path.
9. Dataset replacement now tears down the selection session **before** replacing media/adapter positions, so old numeric positions are never carried across the list identity change.
10. Drag cancellation is scoped to actual selection-session or detached-selection state, avoiding unrelated touch/zoom disturbance during ordinary dataset refreshes with no selection.

## INTENDED BEHAVIOR PRESERVED

The correction intentionally preserves:

- date-section select/deselect toggle;
- cumulative multi-day selection;
- partial and full section counts/icons;
- normal manual long-press selection;
- Commons CAB actions;
- single-pick external-intent guard;
- unrelated Gallery media operations.

## EXACT PRODUCT SCOPE

The merged product delta from `bf08518...` to `5faea376...` contains exactly three modified files:

1. `app/src/main/kotlin/org/fossify/gallery/adapters/MediaAdapter.kt`
2. `app/src/main/kotlin/org/fossify/gallery/views/DateSectionSelectionController.kt`
3. `app/src/main/kotlin/org/fossify/gallery/views/SectionSelectButton.kt`

No Gradle, dependency, workflow, resource, activity, storage, delete, share, edit, or unrelated product file was changed by the fix merge.

## AUTOMATED VALIDATION EVIDENCE

PR workflow run:

`39031654331`

Validated head:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Result:

`PR workflow = SUCCESS`

Individual gates verified:

- change-scope detection: **PASS**
- Gradle wrapper validation: **PASS**
- detekt: **PASS**
- unit test task (`:app:testFossDebugUnitTest`): **PASS**
- Android lint: **PASS**

The separate `Testing build (on PR)` workflow was **skipped** by its own trigger/conditions; this is not recorded as a failure and was not blindly rerun.

The PR was merged only after the final tested head, mergeability, unchanged `main`, and expected-head SHA were reverified.

## WRITE / CONCURRENCY / RECOVERY EVIDENCE

Before Product mutation:

- `main` was freshly verified at `bf08518d5b2902cc9849f156efe25397e4392c09`;
- `AGENTS.md` and the previous canonical continuity record were read;
- no open parallel PR existed;
- the fix was isolated on `fix/gallery-date-section-selection-state-20261017`;
- rollback/recovery remained available through the pre-fix HEAD.

During implementation, two early full-file `MediaAdapter` attempts produced unrelated diff drift. They were detected by read-back/diff audit and were **not accepted**. The isolated branch was reset to the clean point before those drifts, and the final merged delta was rebuilt and audited. None of those rejected drifts reached `main`.

Final merge was guarded with expected PR head:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Actual merge SHA:

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

Post-merge comparison confirmed only the three intended product files changed.

## ACCEPTANCE BOUNDARY

Repository rules remain binding:

**Final Android acceptance is HUMAN-ONLY.**

Automated static/unit/build evidence cannot certify the visual/runtime Android behavior as final PASS/FAIL.

Therefore current wording must be:

**Root fix merged and automated gates passed; final on-device Android acceptance pending.**

Do not say:

- final Android PASS;
- fully accepted on device;
- final Product LKG;

until the owner performs the required human Android verification on a build containing `5faea376...` (or a descendant whose product code is proven equivalent for this fix).

## NEXT ACTIVE WORK

Do **not** reopen RCA.

Next sequence:

1. Fetch current `main` first and reconcile any newer work.
2. Obtain or produce a testable Android build containing the merged root fix using the existing repository release/build path; do not add new infrastructure merely for this.
3. Verify the build/source SHA provenance.
4. Human installs/opens the corrected build on Android.
5. Human reproduces the original high-signal flow, especially selection across a live dataset/list reflow.
6. Confirm that selected media never remain selected without CAB, no ghost `2/53` appears, cumulative section selection still works, and normal manual long-press behavior remains intact.
7. If human acceptance passes, record the accepted build/version/SHA and only then consider promoting the root-fix line to LAST_KNOWN_GOOD.
8. If a new regression appears, preserve evidence and distinguish it from the already-closed RCA before any new mutation.

## NEW-CHAT HANDOFF

A new conversation must start with:

`Fetch current main/state -> read AGENTS.md -> read this PROJECT_STATE -> reconcile -> continue from NEXT ACTIVE WORK.`

Do not rebuild the old RCA from scratch.

Current technical stopping point after this record:

**Root-level correction is merged and CI-validated. Final Android device acceptance is the remaining gate.**
