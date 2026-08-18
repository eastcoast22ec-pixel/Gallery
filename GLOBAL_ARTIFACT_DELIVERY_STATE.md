# Global Artifact Delivery VMAX — Checkpoint

Last verified: 2026-08-18 (Algeria)

Repository: `eastcoast22ec-pixel/Gallery`
Branch: `mp-artifact-courier-v5`
Policy: `GLOBAL_ARTIFACT_DELIVERY_VMAX.md`

## Last verified HEAD before checkpoint write
`4fd30aa733c78dac8254aadaf16a06dad27b258a`

## Architecture
Provider-native first:
- GitHub Actions -> direct GitHub Connector download -> local integrity verification -> `/mnt/data` -> attachment.
- CircleCI -> existing central Auto-Discovery -> Zero-Copy experimental fast path -> verified Relay fallback.
- Future providers -> thin adapter only if necessary.

## Completed
- CircleCI Relay LAST_KNOWN_GOOD preserved.
- Exact CircleCI provider artifact path discovery proved.
- Second independent repository proof completed with `eastcoast22ec-pixel/Gallery` GitHub Actions.
- No new build was triggered for the proof.
- GitHub Actions artifact ZIP digest matched local SHA-256 exactly.
- APK extracted, APK container verified, local APK SHA-256 computed.
- Central global policy created and read back successfully.

## Second-repository proof
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

## Remaining gates
1. Keep GitHub Actions Direct Native as default when Connector is available.
2. Validate CircleCI central profile onboarding on a second CircleCI repository when one exists; do not create a fake repository/build solely for this gate.
3. Keep Zero-Copy experimental until it succeeds end-to-end on two distinct projects.
4. Do not add new infrastructure for these remaining gates.

## Resume
Fetch current HEAD/provider state first -> read `GLOBAL_ARTIFACT_DELIVERY_VMAX.md` + this checkpoint -> reconcile -> continue from the first remaining gate without rebuilding any existing artifact.
