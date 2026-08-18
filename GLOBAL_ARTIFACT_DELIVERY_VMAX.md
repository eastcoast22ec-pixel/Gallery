# Global Artifact Delivery VMAX v1

Status: **ADOPTED ARCHITECTURE / GLOBAL GATES IN PROGRESS**

Core law: **maximum practical power with minimum practical complexity**.

## Control plane

ChatGPT is the primary orchestrator. Do not require manual GitHub/CircleCI/Cloudflare/Opera/Terminal interaction when connected tools or direct APIs can complete the task.

## Fastest provider-native routing

1. **GitHub Actions — Direct Native path (default)**
   - Identify repository/branch/commit.
   - Find the latest relevant successful workflow run.
   - Read provider-returned artifact metadata and exact artifact ID/name.
   - Download directly through the GitHub Connector.
   - Verify provider digest when available.
   - Extract the exact APK, verify APK container and compute local SHA-256.
   - Place the APK in `/mnt/data` and return it as the ChatGPT attachment.
   - **Do not route GitHub Actions artifacts through Courier unless direct native delivery is unavailable.**

2. **CircleCI — Central Auto-Discovery + Courier path**
   - Existing artifact only; never rebuild just to transport it.
   - Auto-discover latest successful pipeline -> workflow -> job -> exact provider artifact path.
   - Bind repository/branch/commit/job/path/size.
   - Try experimental Zero-Copy fast path.
   - On any uncertainty/failure, automatically use the verified Relay fallback.
   - Compute/verify SHA-256 and materialize into ChatGPT.
   - New CircleCI projects should require only one small central policy/profile entry when automatic unambiguous discovery cannot be inferred safely.

3. **Future CI providers — Thin adapter only when proven necessary**
   - Prefer provider-native connected tools/direct APIs.
   - Add no generic proxy, database, queue, worker, scheduler, or agent solely for abstraction.

## Artifact discovery rules

- Never rely on basename alone.
- Use the exact provider-returned path/ID.
- Prefer the latest successful build matching requested repository, branch, artifact type, and release/debug intent.
- Ambiguity fails closed; do not guess between multiple candidate artifacts.
- No manual `artifact_path` should be required from the user.

## Manifest decision

`artifact-manifest.v1.json` is **not mandatory globally**.
Provider metadata + exact identity + local SHA-256 is simpler and already sufficient for GitHub Actions and the proven CircleCI path.
A manifest may be added only when a provider lacks enough immutable identity/integrity metadata or when release provenance requires it.

## Delivery policy

`Existing artifact -> verify source/build/artifact identity -> provider-native direct path when available -> Zero-Copy experimental fast path where applicable -> verified Relay fallback -> SHA-256 -> /mnt/data -> ChatGPT attachment`

Never rebuild solely to move an artifact that already exists.

## Security invariants

- Least privilege.
- Exact repository/branch/commit/build/job/artifact binding.
- HTTPS only.
- No long-lived token in chat, Git, logs, or artifact metadata.
- No unrestricted URL relay or generic download proxy.
- Short-lived/one-time capabilities for Zero-Copy.
- Approved hosts only.
- Fail closed on ambiguity or identity mismatch.
- Local SHA-256 verification before final delivery.

## Recovery / LAST_KNOWN_GOOD

CircleCI Relay success remains LAST_KNOWN_GOOD and must not be removed when Zero-Copy succeeds.

Known proven relay evidence:
- Courier workflow run: `32092943050`
- Artifact ID: `9308946221`
- Extracted APK: `MP-Android-Agent-v0.1.0-debug.apk`
- Size: `886128` bytes
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`

## Second-repository / GitHub Actions live proof

Verified without rebuilding:
- Repository: `eastcoast22ec-pixel/Gallery`
- Source branch: `feature/date-section-toggle-multiselect`
- Source commit: `3751736e5b87091131995021a57c19c49bb5f5c4`
- Successful workflow run: `31785830184` (`Testing build (on PR)`)
- Artifact ID: `9213575879`
- Artifact name: `test-build-foss-debug`
- Provider artifact ZIP digest: `sha256:55bb6b7f7bc94824649d52222112093989ae0c2c11b228290f86eb4e68427c2b`
- Downloaded ZIP local SHA-256: `55bb6b7f7bc94824649d52222112093989ae0c2c11b228290f86eb4e68427c2b` — exact match
- Extracted APK: `gallery-28-foss-debug.apk`
- APK size: `68353614` bytes
- APK container verification: PASS
- APK SHA-256: `72dcbc58b4446c4ed786e1d5f61368d6d80165e2d78f4e2ae00d586fc513974a`

This proves the fastest direct GitHub Actions delivery path on an independent repository with no new build and no new infrastructure.

## Global adoption gates

- [x] Existing CircleCI artifact delivered end-to-end via verified Relay.
- [x] Exact CircleCI artifact path discovery proved; basename-only assumption rejected.
- [x] SHA verification proved.
- [x] No unnecessary rebuild for delivery.
- [x] Second independent repository proof.
- [x] GitHub Actions direct provider-native download proof.
- [x] Free-tier / 0 DZD path.
- [ ] Generalize/validate CircleCI policy onboarding beyond the existing AADL profile when a second CircleCI repository exists.
- [ ] Zero-Copy succeeds end-to-end on two distinct projects before becoming default.

## Global command behavior

When the user says **"أرسل آخر APK"**:

1. Infer the active project from conversation/project context; if ambiguous, inspect accessible repositories and recent project state before asking the user.
2. Detect CI provider from repository/workflow state.
3. Prefer direct provider-native retrieval.
4. Use existing successful artifact; never trigger a build merely for transfer.
5. Discover exact artifact identity/path automatically.
6. Download, verify integrity, place in `/mnt/data`, and attach.
7. If CircleCI Zero-Copy fails, use Relay automatically without user intervention.

## Complexity guard

Do not add infrastructure unless it clearly improves reliability, safety, speed, autonomy, maintainability, cost, or recovery enough to outweigh complexity and failure points.
