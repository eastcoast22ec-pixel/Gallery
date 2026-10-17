<!-- CURRENT-RECONCILIATION-2026-10-17-GALLERY-ROOT-FIX-ACCEPTED -->
# PROJECT_STATE — Gallery

Canonical continuity record for `eastcoast22ec-pixel/Gallery`.

Last reconciliation: **2026-10-17 after human on-device acceptance of the root selection fix**.

## READ THIS FIRST

**STATUS: ROOT-LEVEL FIX ACCEPTED / HUMAN ANDROID PASS / CURRENT PRODUCT LKG ESTABLISHED**

For the confirmed date-section selection / missing-CAB regression:

- **RCA: CLOSED — 100% within the certified defect scope**
- **RCI: COMPLETE**
- **UNKNOWN WITHIN THIS RCA SCOPE: 0**
- **ROOT-LEVEL FIX: IMPLEMENTED AND MERGED**
- **AUTOMATED VALIDATION: PASS**
- **DEBUG APK BUILD / PROVENANCE: PASS**
- **FINAL ANDROID ACCEPTANCE: PASS — HUMAN**
- **REGRESSION STATUS: RESOLVED**

Do not reopen this RCA unless new contradictory runtime evidence appears.

## CURRENT REPOSITORY TRUTH

Repository: `eastcoast22ec-pixel/Gallery`

Stable branch: `main`

Fresh `main` verified immediately before this acceptance write:

`3110d7bb3b2db4b099adf201782870ec973d0ebe`

That HEAD was continuity-only on top of the accepted Product code.

Accepted Product merge on `main`:

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

PR:

`#4 — fix: make date-section selection lifecycle coherent`

Exact Product source SHA used by CI and by the accepted debug APK:

`c459b1826c37cee43ccaa1020329a3677f72d347`

## LAST_KNOWN_GOOD

**LAST_KNOWN_GOOD_PRODUCT:**

`5faea37679bf2449b4acd62e7be501e72e0f3a09`

This merge contains the accepted root-level Product correction.

**ACCEPTED_TEST_APK_SOURCE_SHA:**

`c459b1826c37cee43ccaa1020329a3677f72d347`

Current later `main` commits are continuity-only and preserve this accepted Product tree unless a newer Product mutation is introduced and separately validated.

Pre-fix recovery reference:

`bf08518d5b2902cc9849f156efe25397e4392c09`

Important: `bf08518...` contains the original confirmed CAB/date-section defect. It is a historical recovery reference, **not** the current LKG and must not be restored as a normal rollback target unless deliberately reverting the accepted fix for forensic reasons.

## FULL FORENSIC RCA ARCHIVE

The complete certified RCA remains preserved at:

`bf08518d5b2902cc9849f156efe25397e4392c09:.chatgpt/PROJECT_STATE.md`

It contains the full failure chain, dependency evidence, video evidence, rejected hypotheses, Android touch-dispatch closure, and three clean forensic rounds.

Do not rebuild that investigation from scratch.

## CERTIFIED ROOT CAUSE — COMPACT FORM

Two broken invariants interacted:

1. Non-empty `selectedKeys` was incorrectly treated as equivalent to an active selection ActionMode/CAB.
2. Programmatic date-section selection entered through synthetic `performLongClick()`, which armed RecyclerView drag-selection state independently of ActionMode.

After a real dataset/list reflow, ActionMode could end while stale drag state survived with old numeric positions. The next touch could select a stale range against the new adapter layout, producing the observed detached `2/53`; the invalid selected-key proxy then allowed `53/53` without recreating CAB.

## ROOT-LEVEL CORRECTION

The accepted correction is architectural/general, not a timing patch and not special-cased for 53 items.

- Programmatic section selection no longer uses `performLongClick()`.
- No synthetic human gesture is used to enter selection mode.
- Reflection into Commons selection internals was removed from the date-section controller.
- `MediaAdapter` explicitly owns programmatic section-selection transitions.
- Selection is authoritative only while ActionMode is actually active.
- Detached/split selection state is normalized rather than trusted.
- ActionMode destruction clears RecyclerView selection-gesture state through the normal ACTION_CANCEL lifecycle.
- Dataset replacement tears down selection before media/adapter positions change.
- Drag cancellation is scoped to real selection/detached-selection sessions to preserve unrelated touch/zoom behavior.

Intended behavior preserved:

- date-section select/deselect toggle;
- cumulative multi-day selection;
- partial/full counts and checked state;
- manual long-press selection;
- CAB actions including delete/trash;
- single-pick external-intent guard;
- unrelated Gallery media operations.

## EXACT PRODUCT SCOPE

The accepted fix changed exactly three Product files:

1. `app/src/main/kotlin/org/fossify/gallery/adapters/MediaAdapter.kt`
2. `app/src/main/kotlin/org/fossify/gallery/views/DateSectionSelectionController.kt`
3. `app/src/main/kotlin/org/fossify/gallery/views/SectionSelectButton.kt`

No Gradle, dependency, workflow, activity, storage, delete, share, edit, or unrelated Product file changed in the fix merge.

## AUTOMATED VALIDATION

PR workflow run:

`39031654331`

Validated SHA:

`c459b1826c37cee43ccaa1020329a3677f72d347`

Result:

`SUCCESS`

Verified gates:

- change-scope detection: **PASS**
- Gradle wrapper validation: **PASS**
- detekt: **PASS**
- unit tests: **PASS**
- Android lint: **PASS**

## ACCEPTED DEBUG APK

Workflow:

`Testing build (on PR)`

Run:

`39032130325`

Artifact ID:

`12100444309`

Artifact name:

`test-build-foss-debug`

Exact source SHA:

`c459b1826c37cee43ccaa1020329a3677f72d347`

APK:

`gallery-28-foss-debug.apk`

APK bytes:

`68,353,618`

APK SHA-256:

`7a824ce0a27178b355c2b5d07c439bac18c4f6d5298339e5ee4500f3381da03a`

Artifact ZIP SHA-256 recorded by GitHub and independently matched after download:

`540b882fdcb5c3487cdf9f4ce7968a120822ff9be9e23f6419461d003dcbe807`

The accepted APK is a debug test build, not a signed release/store package.

## HUMAN ANDROID ACCEPTANCE — PASS

Owner reported on 2026-10-17 after testing the corrected APK on the real Android device:

**"تم حل المشكل"**

This satisfies the repository's HUMAN-ONLY final Android acceptance rule.

Acceptance recording supplied in the same conversation:

`1000038178.mp4`

Recording metadata / fingerprint:

- bytes: `44,537,707`
- dimensions: `720 x 1584`
- frames: `2,962`
- duration: `48.151478 s`
- SHA-256: `901528ed9934c899ec5a07533e4f41581bb7e467ff47f85c79e864cdb4964bdf`

Sampled frames are consistent with the reported successful behavior, including selected date sections with the contextual action bar and trash action present. The decisive acceptance authority is the human owner's on-device statement, per `AGENTS.md`.

Accepted invariants:

- CAB/trash is present when section-selected media are selected;
- the original detached-CAB failure is no longer reproduced;
- the root-fix line is accepted on the real Android surface.

## WRITE / RECOVERY NOTES

All Product mutation occurred on an isolated fix branch and was merged only after exact-SHA validation.

Rejected full-file drift attempts were detected by diff audit before merge and never reached `main`.

The test build reused the repository's existing workflow; no new CI/build infrastructure was added.

The temporary `testers needed` label used to trigger the exact-SHA debug build was removed after successful artifact creation.

## NEXT ACTIVE WORK

This defect is **closed**.

For future Gallery work:

1. Fetch fresh `main` and current state first.
2. Preserve `5faea376...` / Product source `c459b182...` as the accepted LKG until a newer Product change passes its own validation and human acceptance where required.
3. Do not reopen this CAB/date-section RCA without new contradictory evidence.
4. If a different regression appears, create a new scoped RCI instead of mixing it into this closed failure chain.

## NEW-CHAT HANDOFF

Start with:

`Fetch current main/state -> read AGENTS.md -> read PROJECT_STATE -> reconcile -> continue.`

Current technical state:

**The root-level date-section/CAB defect is resolved, validated, human-accepted, and promoted to the current Product LKG.**
