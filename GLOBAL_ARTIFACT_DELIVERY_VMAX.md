# Global Artifact Delivery VMAX v1

Status: **GLOBAL STANDARD v1 ADOPTED**

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
- Verified implementation HEAD: `39d5a381b944131c8f6f856b0dddbcf86088e6dc`
- Production CI: `zr-python-ci` Job `1258` SUCCESS; `zr-cloudflare-ci` Job `1259` SUCCESS.

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

CircleCI Relay is the permanent recovery path until a stronger path is independently proven.

Original proven relay evidence:
- Courier run: `32092943050`
- Artifact ID: `9308946221`
- APK: `MP-Android-Agent-v0.1.0-debug.apk`
- Size: `886128` bytes
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`

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
7. If CircleCI Zero-Copy is unavailable or uncertain, use Relay automatically without user intervention.

## Complexity guard

Do not add infrastructure unless it clearly improves reliability, safety, speed, autonomy, maintainability, cost, or recovery enough to outweigh complexity and failure points.
