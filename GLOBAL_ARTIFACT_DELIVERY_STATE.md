# Global Artifact Delivery VMAX — Checkpoint

Last verified: 2026-08-18 (Algeria)

Repository: `eastcoast22ec-pixel/Gallery`
Branch: `mp-artifact-courier-v5`
Policy: `GLOBAL_ARTIFACT_DELIVERY_VMAX.md`
Status: **GLOBAL STANDARD v1 ADOPTED**

## Current source of truth

Global policy commit before this checkpoint write:
`62b0afb99bffb0e07355f77f99a87be9684da894`

CircleCI Auto-Discovery implementation:
- Repository: `eastcoast22ec-pixel/mirage-pub-cloud-runner`
- Production branch: `mp-export-zr-connector`
- Verified HEAD: `39d5a381b944131c8f6f856b0dddbcf86088e6dc`
- CI: Job `1258` SUCCESS, Job `1259` SUCCESS.

## Architecture

Provider-native first:
- GitHub Actions -> direct GitHub Connector download -> local integrity verification -> `/mnt/data` -> attachment.
- CircleCI -> central Auto-Discovery -> Zero-Copy experimental fast path -> verified Relay fallback.
- Future providers -> thin adapter only if necessary.

## Completed

- CircleCI Relay LAST_KNOWN_GOOD preserved.
- Exact CircleCI provider artifact path discovery proved.
- CircleCI Auto-Discovery integrated into `artifact-direct-ticket`.
- MP Android Agent Auto-Discovery proved live without Job/path input.
- AADL Auto-Discovery proved live without Job/path input.
- Independent GitHub Actions repository proof completed with `eastcoast22ec-pixel/Gallery`.
- SHA-256 and APK container verification proved.
- No new Android build was triggered for any delivery proof.
- Global policy updated and adopted as Global Standard v1.

## CircleCI Auto-Discovery proof — MP Android Agent

- selector: `__auto__/mp-agent/latest-stable.apk`
- Courier run: `32095962500`
- Artifact ID: `9309935846`
- APK: `MP-Android-Agent-v0.1.0-debug.apk`
- size: `886128`
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`
- mode: `relay`
- APK container: PASS

## CircleCI Auto-Discovery proof — AADL

- selector: `__auto__/aadl/latest-stable.apk`
- Courier run: `32096044401`
- Artifact ID: `9309960665`
- APK: `AADL-QuickPay-v0.3.17-stable.apk`
- size: `2750002`
- SHA-256: `cf3d968d0ca8cb8d584b4f3c9797496e6c56c14a7586c275c5b48ad2efdfbe1b`
- mode: `relay`
- APK container: PASS

## Independent GitHub Actions proof

- Repository: `eastcoast22ec-pixel/Gallery`
- Source commit: `3751736e5b87091131995021a57c19c49bb5f5c4`
- Workflow run: `31785830184`
- Artifact ID: `9213575879`
- Artifact: `test-build-foss-debug`
- ZIP SHA-256: `55bb6b7f7bc94824649d52222112093989ae0c2c11b228290f86eb4e68427c2b`
- APK: `gallery-28-foss-debug.apk`
- APK size: `68353614`
- APK SHA-256: `72dcbc58b4446c4ed786e1d5f61368d6d80165e2d78f4e2ae00d586fc513974a`

## LAST_KNOWN_GOOD

CircleCI Courier/Relay:
- Run `32092943050`
- Artifact ID `9308946221`
- APK SHA-256 `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`

Production source rollback target before Auto-Discovery direct integration:
- `8a728301b9212c29ad96509ae3d8cf9de04154c1`

## Remaining non-blocking gates

1. If a genuinely separate CircleCI repository is added, onboard it with one minimal profile and validate using an existing artifact; do not create a fake build solely for proof.
2. Keep Zero-Copy experimental until it succeeds end-to-end on two distinct projects.
3. Do not remove Relay until a stronger replacement is independently proven.
4. Do not add infrastructure for these gates.

## Next action

Normal operation may now use the global command behavior: `أرسل آخر APK` -> infer project -> detect provider -> auto-discover latest successful artifact -> deliver -> verify SHA-256 -> attach.

For future development, fetch current HEAD/provider state first, read `GLOBAL_ARTIFACT_DELIVERY_VMAX.md` + this checkpoint, reconcile, and continue only from a still-valid non-blocking gate.
