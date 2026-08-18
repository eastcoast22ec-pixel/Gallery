# Global Artifact Delivery VMAX v1

Status: **GLOBAL STANDARD v1 ADOPTED + UNIVERSAL TRANSFER RULE ACTIVE**

Core law: **maximum practical power with minimum practical complexity**.

Zero-Copy remains an experimental optimization; it is **not** required for standard operation. The verified Relay remains LAST_KNOWN_GOOD for CircleCI.

## Control plane

ChatGPT is the primary orchestrator. Do not require manual GitHub/CircleCI/Cloudflare/Opera/Terminal interaction when connected tools or direct APIs can complete the task.

## Provider-native routing

1. **GitHub Actions — Direct Native path (default)**
   - Identify repository/branch/commit.
   - Find the latest relevant successful workflow run.
   - Read provider-returned artifact metadata and exact artifact ID/name.
   - Download directly through the GitHub Connector.
   - Verify provider digest when available.
   - Extract the exact APK, verify APK container, compute local SHA-256.
   - Place the APK in `/mnt/data` and return it as the ChatGPT attachment.
   - Do not route GitHub Actions artifacts through Courier unless direct native delivery is unavailable.

2. **CircleCI — Central Auto-Discovery + Courier path**
   - Existing artifact only; never rebuild merely to transport it.
   - User-facing selector may be only `latest APK` / `latest stable APK`; no Job number or artifact path is required.
   - Auto-discover latest successful pipeline -> workflow -> job -> exact provider artifact path -> size.
   - Bind repository/branch/commit/job/path/size before delivery.
   - Try Zero-Copy only as a fast-path optimization.
   - On any uncertainty/failure, automatically use the verified Relay fallback.
   - Verify APK container and SHA-256 before final delivery.
   - New CircleCI projects require only one small central profile when discovery cannot be inferred safely and unambiguously.

3. **Future CI providers — Thin adapter only when necessary**
   - Prefer provider-native connected tools/direct APIs.
   - Add no generic proxy, database, queue, worker, scheduler, or agent solely for abstraction.

## Artifact discovery rules

- Never rely on basename alone.
- Use the exact provider-returned path/ID.
- Prefer the latest successful build matching requested repository, branch, artifact type, and release/debug intent.
- Ambiguity fails closed; do not guess between multiple candidate artifacts.
- No manual `artifact_path` or Job number should be required from the user.

## Manifest decision

`artifact-manifest.v1.json` is **not mandatory globally**.
Provider metadata + exact identity + local SHA-256 is simpler and sufficient for the proven GitHub Actions and CircleCI paths.
Add a manifest only when a provider lacks enough immutable identity/integrity metadata or release provenance specifically requires it.

## Delivery policy

`Existing artifact -> verify source/build/artifact identity -> provider-native direct path when available -> CircleCI Auto-Discovery -> Zero-Copy experimental fast path -> verified Relay fallback -> SHA-256 -> /mnt/data -> ChatGPT attachment`

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

## Production implementation

CircleCI Auto-Discovery source of truth:
- Repository: `eastcoast22ec-pixel/mirage-pub-cloud-runner`
- Production branch: `mp-export-zr-connector`
- Verified implementation HEAD: `2a30a0ba43d1220c514e2f62967cf96b919a1c48`
- Production CI: `zr-python-ci` Job `1261` SUCCESS; `zr-cloudflare-ci` Job `1262` SUCCESS.
- Zero-Copy control transport staging gate: Job `1260` SUCCESS.

Current CircleCI profiles:
- `aadl` / `quickpay` / `aadl-quickpay`
- `mp-agent` / `mp-android-agent` / `android-agent`

## Verified CircleCI Auto-Discovery proofs

### MP Android Agent
Request contained no real Job number/path:
- selector: `__auto__/mp-agent/latest-stable.apk`
- Courier run: `32095962500`
- Artifact ID: `9309935846`
- Discovered APK: `MP-Android-Agent-v0.1.0-debug.apk`
- Size: `886128` bytes
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`
- APK container: PASS
- Delivery mode: `relay`
- New Android build triggered: NO

### AADL QuickPay
Request contained no real Job number/path:
- selector: `__auto__/aadl/latest-stable.apk`
- Courier run: `32096044401`
- Artifact ID: `9309960665`
- Discovered APK: `AADL-QuickPay-v0.3.17-stable.apk`
- Size: `2750002` bytes
- SHA-256: `cf3d968d0ca8cb8d584b4f3c9797496e6c56c14a7586c275c5b48ad2efdfbe1b`
- APK container: PASS
- Delivery mode: `relay`
- New Android build triggered: NO

## Zero-Copy status

A confirmed transport mismatch was corrected: production uses `CIRCLECI_CONTROL_TRANSPORT=render-relay`, so Zero-Copy control/API reads now use that configured transport while CircleCI artifact-host fetches remain direct.

Validation:
- isolated staging CI Job `1260`: SUCCESS
- production CI Jobs `1261` / `1262`: SUCCESS
- bounded live MP Agent retry: Courier run `32096595250`, Artifact ID `9310138435`
- resulting APK size/hash/container still verified exactly
- resulting delivery mode: `relay`

Decision: **do not add further infrastructure or complexity solely to force Zero-Copy now**. Relay is already reliable, verified, free-tier compatible, and fully automatic. Zero-Copy stays a non-blocking experimental optimization until a future provider/runtime signal gives a clear, low-complexity path to prove it.

## Verified GitHub Actions independent-repository proof

- Repository: `eastcoast22ec-pixel/Gallery`
- Source branch: `feature/date-section-toggle-multiselect`
- Source commit: `3751736e5b87091131995021a57c19c49bb5f5c4`
- Successful workflow run: `31785830184`
- Artifact ID: `9213575879`
- Artifact name: `test-build-foss-debug`
- Provider ZIP digest: `sha256:55bb6b7f7bc94824649d52222112093989ae0c2c11b228290f86eb4e68427c2b`
- Downloaded ZIP SHA-256: `55bb6b7f7bc94824649d52222112093989ae0c2c11b228290f86eb4e68427c2b`
- APK: `gallery-28-foss-debug.apk`
- APK size: `68353614` bytes
- APK container: PASS
- APK SHA-256: `72dcbc58b4446c4ed786e1d5f61368d6d80165e2d78f4e2ae00d586fc513974a`
- New build triggered for delivery proof: NO

## Recovery / LAST_KNOWN_GOOD

CircleCI Relay is the permanent recovery/default delivery path until a stronger path is independently proven.

Latest verified automatic relay proof:
- Courier run: `32096595250`
- Artifact ID: `9310138435`
- APK: `MP-Android-Agent-v0.1.0-debug.apk`
- Size: `886128` bytes
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`
- APK container: PASS

Original proven relay evidence remains historical LKG evidence:
- Courier run: `32092943050`
- Artifact ID: `9308946221`

## Global adoption gates

- [x] Existing CircleCI artifact delivered end-to-end via verified Relay.
- [x] Exact CircleCI provider path discovery proved; basename-only assumption rejected.
- [x] CircleCI Auto-Discovery integrated into the direct delivery entrypoint.
- [x] CircleCI Auto-Discovery proved live on MP Android Agent without Job/path input.
- [x] CircleCI Auto-Discovery proved live on AADL without Job/path input.
- [x] SHA verification proved.
- [x] No unnecessary rebuild for delivery.
- [x] Independent repository proof completed.
- [x] GitHub Actions Direct Native download proof completed.
- [x] Free-tier / 0 DZD path.
- [x] Global Standard v1 adopted for the currently available provider set.
- [ ] When a genuinely separate CircleCI repository exists, onboard it with one minimal profile and validate without creating a fake build solely for this gate.
- [ ] Zero-Copy must succeed end-to-end on two distinct projects before it may replace Relay as the default CircleCI path.

The two remaining items are **non-blocking future optimization/onboarding gates**, not blockers for Global Standard v1.

## Global command behavior

When the user says **"أرسل آخر APK"**:

1. Infer the active project from project/conversation context; if ambiguous, inspect accessible repositories and recent project state before asking the user.
2. Detect the CI provider from repository/workflow state.
3. Prefer provider-native direct retrieval.
4. Use an existing successful artifact; never trigger a build merely for transfer.
5. Discover exact artifact identity/path automatically.
6. Download, verify integrity, place in `/mnt/data`, and attach.
7. CircleCI uses the verified Relay automatically; Zero-Copy may be attempted only as a safe optimization and must never block delivery.

## Universal transfer decision rule — mandatory for future transfer problems

This rule applies to **all similar transfer problems**, not only APK delivery and not only CircleCI. It covers repository-to-repository transfer, CI-to-ChatGPT delivery, provider-to-provider migration, staging materialization, ZIP/JSON/PDF/APK delivery, and any future artifact or source-tree movement where exact bytes matter.

### Core rule

**Do not invent a new transfer architecture while an existing verified transport can carry the required bytes safely.**

Always start from the latest verified source and reuse the smallest existing transport. Transfer is a data-movement problem first, not an architecture problem.

### Mandatory order

1. **Reuse already-materialized target content**
   - Read target state first.
   - Do not retransmit files/subtrees already present with matching Git blob/tree SHA or provider digest.
   - Move only the missing delta.

2. **Provider-native direct transfer**
   - Prefer connected provider APIs/connectors and exact artifact IDs/paths.
   - GitHub -> GitHub Connector/Data API.
   - GitHub Actions -> direct workflow artifact download.
   - CircleCI -> existing artifact API/Auto-Discovery.
   - Never rebuild just to obtain a transferable copy when an existing successful artifact exists.

3. **Reuse the verified Global Artifact Delivery lane**
   - For CircleCI or similar providers, prefer the existing Auto-Discovery + Relay/Courier path.
   - Zero-Copy is optional only; failure or incompatibility must fall back to Relay rather than cause new infrastructure.
   - Existing Relay/Courier may carry any explicitly allowed safe artifact type; extend an allowlist only when the need is real and the change is smaller/safer than building a separate transport.

4. **ChatGPT as a verified bridge when necessary**
   - Provider artifact -> ChatGPT `/mnt/data` -> verify container/digest -> target provider/repository via connected API.
   - This is preferred over asking the user to download/upload files manually.
   - Preserve exact bytes and verify SHA-256 before and after the bridge.

5. **Content-addressed repository transfer**
   - When moving source trees through GitHub APIs, reuse existing target blobs/trees first.
   - For missing objects, create blobs/trees with integrity gates and do not move the target branch ref until the complete tree is verified.
   - Cross-repository Git object SHA reuse must never be assumed; GitHub object stores are repository-scoped unless live API proof says otherwise.

6. **Existing bounded CI transfer job only if provider-native paths cannot carry the bytes**
   - One isolated export/import job, no build/deploy/runtime mutation.
   - Verify exact source tree/commit before packaging.
   - Store one deterministic artifact and verify its digest.
   - Never rerun a failed deterministic transfer job without fixing the proven cause first.

7. **Temporary adapter only as last automated fallback**
   - Must be read-only toward source and shadow-only toward target until equivalence passes.
   - No generic proxy/database/queue/worker/agent unless the transfer cannot be solved with existing lanes.
   - Remove temporary transfer components after success unless they have proven recurring value.

8. **Manual user action is the final fallback**
   - Only after connected tools, direct APIs, existing Relay/Courier, ChatGPT bridge, GitHub Data API, and bounded CI transfer have been exhausted.
   - Ask for the minimum possible phone-only action.
   - Never ask for a PC/Terminal/Opera when a connected path can solve the transfer.

### Integrity gate

For every transfer, bind and verify as many immutable identities as the source provides:

`source repository/provider -> branch/ref -> commit/tree SHA -> build/job/run ID -> exact artifact path/ID -> size -> provider digest -> local SHA-256 -> target blob/tree/artifact identity`

A transfer is not complete until the target is read back and identity/equivalence is verified. Timeout or missing status is not evidence of failure; read actual target state before retrying.

### Duplicate-work guard

Before every new transfer attempt:

- inspect destination first;
- inspect prior successful artifacts/jobs/runs;
- reuse existing bytes if available;
- do not repeat successful export/download/materialization steps;
- do not create a second transport while the first verified transport still has a viable continuation path.

### Complexity gate

A new transfer component is permitted only if it clearly improves reliability, safety, speed, autonomy, maintainability, cost, or recovery **more than** the complexity and new failure modes it adds.

If the answer is not clearly yes: **reuse the existing transport**.

## Complexity guard

Do not add infrastructure unless it clearly improves reliability, safety, speed, autonomy, maintainability, cost, or recovery enough to outweigh complexity and failure points.
