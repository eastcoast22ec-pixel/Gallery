# Global Artifact Delivery VMAX — Checkpoint

Last verified: 2026-08-18 (Algeria)

Repository: `eastcoast22ec-pixel/Gallery`
Branch: `mp-artifact-courier-v5`
Policy: `GLOBAL_ARTIFACT_DELIVERY_VMAX.md`
Status: **GLOBAL STANDARD v1 ADOPTED / VERIFIED RELAY-FIRST / UNIVERSAL TRANSFER RULE ACTIVE**

## Current source of truth

Global policy commit:
`f393e3541b3bf540fed85dbda10f0c7d968deac0`

CircleCI implementation:
- Repository: `eastcoast22ec-pixel/mirage-pub-cloud-runner`
- Production branch: `mp-export-zr-connector`
- Verified baseline HEAD: `2a30a0ba43d1220c514e2f62967cf96b919a1c48`
- Production CI: Job `1261` SUCCESS; Job `1262` SUCCESS.
- Isolated Zero-Copy transport gate: Job `1260` SUCCESS.

## Architecture

Provider-native first:
- GitHub Actions -> direct GitHub Connector download -> local integrity verification -> `/mnt/data` -> attachment.
- CircleCI -> central Auto-Discovery -> optional Zero-Copy attempt -> verified Relay default/fallback.
- Future providers -> thin adapter only if necessary.

Universal transfer decision rule is now mandatory for all similar transfer work:
`inspect destination -> reuse already-present bytes -> provider-native transfer -> existing Relay/Courier -> ChatGPT verified bridge -> GitHub content-addressed API -> bounded isolated CI transfer -> temporary adapter -> minimal manual action only as final fallback`.

## Completed

- CircleCI Relay LAST_KNOWN_GOOD preserved.
- Exact CircleCI provider artifact path discovery proved.
- CircleCI Auto-Discovery integrated into `artifact-direct-ticket`.
- MP Android Agent Auto-Discovery proved live without Job/path input.
- AADL Auto-Discovery proved live without Job/path input.
- Independent GitHub Actions repository proof completed with `eastcoast22ec-pixel/Gallery`.
- SHA-256 and APK container verification proved.
- No new Android build was triggered for any delivery proof.
- Zero-Copy control transport mismatch corrected and tested.
- Bounded live retry after the fix still selected Relay, so no further complexity was added.
- Global policy frozen as verified Relay-first Global Standard v1.
- Universal transfer decision rule adopted for repository-to-repository, CI-to-ChatGPT, provider-to-provider, staging materialization, and generic artifact/source-tree movement.
- Duplicate-work guard adopted: inspect destination and prior artifacts before any new transfer attempt; reuse verified bytes instead of rebuilding/re-exporting.
- Integrity chain adopted: bind source repository/provider, ref, commit/tree, run/job/artifact identity, size/digest, local SHA-256, and target identity whenever available.
- ZR extraction immediately reused this rule: existing target metadata was preserved and only the two missing application subtrees remain to be materialized.

## CircleCI Auto-Discovery proof — MP Android Agent

- selector: `__auto__/mp-agent/latest-stable.apk`
- initial Auto-Discovery Courier run: `32095962500`
- Artifact ID: `9309935846`
- APK: `MP-Android-Agent-v0.1.0-debug.apk`
- size: `886128`
- SHA-256: `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`
- mode: `relay`
- APK container: PASS

Latest bounded Zero-Copy validation retry:
- Courier run: `32096595250`
- Artifact ID: `9310138435`
- same APK / same size / same SHA-256
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

Current automatic CircleCI Relay LKG:
- Run `32096595250`
- Artifact ID `9310138435`
- APK SHA-256 `e0cb767760bcc07413f926889cf143b2c44b83bae878943fb044f6530d00fd38`

Historical relay proof:
- Run `32092943050`
- Artifact ID `9308946221`

Production source rollback targets:
- before Zero-Copy transport patch: `39d5a381b944131c8f6f856b0dddbcf86088e6dc`
- before Auto-Discovery direct integration: `8a728301b9212c29ad96509ae3d8cf9de04154c1`

## Remaining non-blocking gates

1. If a genuinely separate CircleCI repository is added, onboard it with one minimal profile and validate using an existing artifact; do not create a fake build solely for proof.
2. Keep Zero-Copy experimental until it succeeds end-to-end on two distinct projects.
3. Do not remove Relay until a stronger replacement is independently proven.
4. Do not add infrastructure solely to force Zero-Copy.
5. Extend artifact-type allowlists only for a real transfer need and only when reusing the verified lane is smaller and safer than creating a separate transport.

## Next action

Normal operation is now the priority. The global transfer rule applies automatically to future transfer problems.

For artifact delivery:
`infer context -> inspect destination/prior successful artifacts -> provider-native retrieval -> existing verified Relay/Courier if needed -> verify SHA-256 -> materialize target -> read back target identity`.

Future development should fetch current HEAD/provider state first, read `GLOBAL_ARTIFACT_DELIVERY_VMAX.md` + this checkpoint, reconcile, and continue only from a still-valid non-blocking gate.
