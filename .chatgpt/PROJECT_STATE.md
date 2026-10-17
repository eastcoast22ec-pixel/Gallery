<!-- CURRENT-RECONCILIATION-2026-10-17-GALLERY-ROOT-FIX-BUILT -->
# PROJECT_STATE — Gallery

Canonical continuity record for `eastcoast22ec-pixel/Gallery`.

Last reconciliation: **2026-10-17 after root-fix merge and verified debug build**.

## READ THIS FIRST

**STATUS: ROOT-LEVEL FIX MERGED / CI PASS / DEBUG APK BUILT AND VERIFIED / FINAL ANDROID ACCEPTANCE PENDING HUMAN**

For the confirmed date-section selection / missing-CAB regression:

- **RCA: CLOSED — 100% within the certified defect scope**
- **RCI: COMPLETE**
- **UNKNOWN WITHIN THIS RCA SCOPE: 0**
- **ROOT-LEVEL FIX: IMPLEMENTED AND MERGED TO `main`**
- **AUTOMATED VALIDATION: PASS**
- **DEBUG TEST BUILD: PASS / ARTIFACT VERIFIED**
- **FINAL ANDROID ACCEPTANCE: PENDING — HUMAN ONLY**

Do **not** reopen the RCA unless new contradictory runtime evidence appears.
Do **not** claim final Android PASS or promote the fix to final Android LAST_KNOWN_GOOD until a human verifies the corrected APK on-device, per `AGENTS.md`.

## CURRENT REPOSITORY TRUTH

Repository: `eastcoast22ec-pixel/Gallery`

Stable branch: `main`

Product-code merge containing the root fix:

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

PR:

`#4 — fix: make date-section selection lifecycle coherent`

Validated Product head used by CI and the test APK:

`c459b1826c37cee43ccaa1020329a3677f72d347`

First post-merge continuity commit:

`5fa97d18429e161b0059d28e1bfda84f8d90b32e`

Pre-fix continuity HEAD / recovery target:

`bf08518d5b2902cc9849f156efe25397e4392c09`

Important recovery note: `bf08518...` is a valid repository rollback target if the correction introduces a new regression, but it contains the already-confirmed original CAB/date-section defect. It is **not** a defect-free Product LKG.

## FULL FORENSIC RCA ARCHIVE

The complete certified forensic RCA remains immutably preserved in Git at:

`bf08518d5b2902cc9849f156efe25397e4392c09:.chatgpt/PROJECT_STATE.md`

That archive contains the full video evidence chain, exact source/dependency facts, rejected hypotheses, Android touch-dispatch closure, and three independent clean forensic rounds. Do not rebuild that investigation from scratch.

## USER RUNTIME EVIDENCE RETAINED

Affected owner-reported build:

`1.13.1-nightly-3751736`

Investigated source revision:

`3751736e5b87091131995021a57c19c49bb5f5c4`

Regression video:

`1000038169.mp4`

Verified video SHA-256:

`40fcd4737334273bde3c7b17e052794dcfd0d51a483f0391258047eb758d561f`

Observed decisive sequence:

`53/53 with CAB -> dataset/list reflow -> 2/53 without CAB -> 53/53 without CAB -> zero -> later fresh selection recreates CAB`

## CERTIFIED ROOT CAUSE — COMPACT FORM

### Root cause A — invalid selection-mode proxy

The feature treated non-empty `selectedKeys` as equivalent to live selection ActionMode/CAB. That is false because Commons owns selected-key storage and ActionMode lifecycle as separate state.

### Root cause B — programmatic selection entered through a synthetic human gesture

Date-section selection used `performLongClick()`. Commons then executed:

`viewLongClicked() -> itemLongClicked() -> recyclerView.setDragSelectActive(position)`

Because Android posts the section button click after the physical ACTION_UP path, the synthetic long-click could arm RecyclerView drag state after that physical ACTION_UP had already occurred. A later dataset/list reflow could finish ActionMode while the separately owned drag state survived with an old numeric position. The next touch then selected a stale range against new positions, producing `2/53` without CAB. The invalid `selectedKeys` proxy then allowed `53/53` without recreating CAB.

## DEPENDENCY FACTS

Gallery pin used by the RCA:

`Fossify Commons 6.1.6`

Exact upstream source commit:

`cbee1f1385a006f9f82c1a2c8b3722b5f0a242bd`

Authoritative upstream facts:

- `selectedKeys`, `actMode`, `actModeCallback.isSelectable`, and RecyclerView drag state are distinct state;
- `toggleItemSelection(...)` can mutate keys without requiring live ActionMode;
- manual long-click enters ActionMode and then arms drag selection;
- RecyclerView ACTION_UP/ACTION_CANCEL clears drag state;
- Commons ActionMode teardown does not itself reset RecyclerView drag state.

## ROOT-LEVEL CORRECTION IMPLEMENTED

This is not a timing workaround and is not special-cased for 53 items.

1. Programmatic date-section selection no longer uses `performLongClick()`.
2. Section selection no longer searches/scrolls for a ViewHolder or retries layout to synthesize a gesture.
3. `DateSectionSelectionController` no longer uses reflection into Commons selection internals.
4. `MediaAdapter` is the explicit owner of programmatic section-selection transitions because it already owns the inherited ActionMode and selected-key state.
5. Selection is considered authoritative only while:

`actMode != null && actModeCallback.isSelectable`

6. Programmatic selection starts ActionMode directly through its callback and then uses the inherited protected selection API.
7. Detached/split selection state is defensively normalized instead of being treated as a valid CAB session.
8. ActionMode destruction cancels RecyclerView selection-gesture state through the RecyclerView ACTION_CANCEL lifecycle path.
9. Dataset replacement tears down selection **before** replacing media/adapter positions, preventing old numeric drag positions from crossing list identity changes.
10. Drag cancellation is scoped to actual selection-session/detached-selection state so ordinary no-selection dataset refreshes do not unnecessarily disturb unrelated touch/zoom behavior.

## INTENDED BEHAVIOR PRESERVED

The correction preserves:

- date-section select/deselect toggle;
- cumulative multi-day selection;
- partial/full section counts and icons;
- normal manual long-press selection;
- Commons CAB actions;
- single-pick external-intent guard;
- unrelated Gallery media operations.

## EXACT PRODUCT SCOPE

Post-merge comparison from pre-fix `bf08518...` to Product merge `5faea376...` confirmed exactly three Product files changed:

1. `app/src/main/kotlin/org/fossify/gallery/adapters/MediaAdapter.kt`
2. `app/src/main/kotlin/org/fossify/gallery/views/DateSectionSelectionController.kt`
3. `app/src/main/kotlin/org/fossify/gallery/views/SectionSelectButton.kt`

No Gradle, dependency, workflow, resource, activity, storage, delete, share, edit, or unrelated Product file changed in the fix merge.

The first post-merge continuity commit changed only:

`.chatgpt/PROJECT_STATE.md`

## AUTOMATED VALIDATION EVIDENCE

PR workflow run:

`39031654331`

Validated head:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Overall result:

`SUCCESS`

Verified gates:

- change-scope detection: **PASS**
- Gradle wrapper validation: **PASS**
- detekt: **PASS**
- unit test task `:app:testFossDebugUnitTest`: **PASS**
- Android lint: **PASS**

The PR was merged only after CI success, unchanged base, mergeability, and expected-head SHA were freshly verified.

## VERIFIED DEBUG APK BUILD

Existing repository workflow was reused; no new build infrastructure was added.

Workflow:

`Testing build (on PR)`

Run:

`39032130325`

Trigger:

Existing `testers needed` label on merged PR #4. This allowed a test build from the exact already-tested PR head without a new Product commit or duplicate fix branch.

Source SHA embedded into the nightly build path:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Build steps verified:

- checkout/setup/cache: **PASS**
- clean: **PASS**
- nightly version derivation: **PASS**
- `assembleFossDebug`: **PASS**
- debug artifact upload: **PASS**
- release signing/release APK steps: **SKIPPED by design**

GitHub artifact:

- artifact ID: `12100444309`
- artifact name: `test-build-foss-debug`
- artifact ZIP bytes: `45,229,065`
- artifact ZIP SHA-256: `540b882fdcb5c3487cdf9f4ce7968a120822ff9be9e23f6419461d003dcbe807`
- workflow head SHA: `c459b1826c37cee43ccaa1020329a3677f72d347`

Downloaded ZIP digest was independently recomputed and matched GitHub's recorded digest exactly.

APK inside artifact:

`gallery-28-foss-debug.apk`

APK bytes:

`68,353,618`

APK SHA-256:

`7a824ce0a27178b355c2b5d07c439bac18c4f6d5298339e5ee4500f3381da03a`

This APK is a debug test build, not a signed release/store update.

## WRITE / RECOVERY NOTES

Before Product mutation, fresh `main`, `AGENTS.md`, canonical continuity, parallel PR state, Product blobs, and rollback path were verified.

Two early full-file `MediaAdapter` attempts produced unrelated diff drift. Read-back/diff audit detected them before merge. The isolated fix branch was reset to the clean point and the final merged delta was rebuilt and audited. None of the rejected drift reached `main`.

Final merge was guarded by exact expected head:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Actual Product merge:

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

## ACCEPTANCE BOUNDARY

Repository rule remains binding:

**Final Android acceptance is HUMAN-ONLY.**

Current legal wording:

**Root fix merged, automated gates passed, exact-SHA debug APK built and provenance verified; final on-device Android acceptance pending.**

Do not state final Android PASS or final Product LKG until human verification succeeds.

## NEXT ACTIVE WORK

Do **not** reopen RCA.

1. Fetch current `main` first if any new session begins.
2. Human installs/opens the verified debug APK built from `c459b182...`.
3. Human reproduces the original high-signal flow, especially date-section selection across a live dataset/list reflow.
4. Acceptance checks:
   - CAB/trash remains present whenever section-selected media are selected;
   - no ghost `2/53` appears after reflow;
   - no `53/53` exists without CAB;
   - deselection returns coherently to zero;
   - cumulative multi-day selection still works;
   - manual long-press selection still works normally.
5. If human acceptance passes, record the accepted APK SHA/version/source SHA and only then promote the corrected line to LAST_KNOWN_GOOD.
6. If a new regression appears, preserve evidence and distinguish it from the already-closed RCA before any new mutation.

## NEW-CHAT HANDOFF

Start with:

`Fetch current main/state -> read AGENTS.md -> read PROJECT_STATE -> reconcile -> continue from NEXT ACTIVE WORK.`

Do not start from zero and do not rebuild the old RCA.

Current technical stopping point:

**The verified debug APK is ready. Final human Android acceptance is the remaining gate.**
