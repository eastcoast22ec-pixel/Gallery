# Repository execution rules — Gallery

1. Live GitHub/runtime/user evidence is the highest source of truth.
2. `main` is the stable repository branch. Current `.chatgpt/PROJECT_STATE.md` defines whether Gallery is active, dormant, conditional, or closed after fresh reconciliation.
3. Before any consequential write, fetch current `main`, inspect parallel work, read `.chatgpt/PROJECT_STATE.md`, resolve the exact scope, and preserve recovery evidence.
4. Historical branches/checkpoints are non-authoritative by default and must not create backlog or be deleted by name/age alone.
5. Do not build, deploy, rerun, or create infrastructure merely to refresh continuity or save documentation.
6. Preserve 0 DZD / free-tier-first operation and keep secrets/credentials out of Git, chat, logs, and artifacts.
7. **Future-plan placement:** when the owner asks to save/pin/freeze/preserve a plan for later work, first read canonical `eastcoast22ec-pixel/mp-vmax-policy:main:.chatgpt/FUTURE_PLAN_PLACEMENT_POLICY.md`. Follow its fresh owner-repository -> `plan/*` -> `.chatgpt/plans/*` -> exact Master Portfolio pointer flow. Saving a plan does not activate it or authorize CI/Build/Deploy/provider mutation.
