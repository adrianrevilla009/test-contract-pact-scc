# openapi-breaking-changes

A small Python detector (`breaking.py`) and three Orders OpenAPI specs: a v1 baseline, a compatible v2 and a breaking v2.

## Goal
Detect breaking changes between two versions of an OpenAPI spec, the spec-driven way to protect consumers you cannot enumerate.

## Run it
```bash
bash verify.sh
python3 breaking.py orders-v1.json orders-v2-breaking.json
```
Expected from the second command (exit code 1):
```
BREAKING: GET /orders/{id}: parameter 'id' type changed
BREAKING: GET /orders/{id}: new required query parameter 'tenant'
...
6 breaking change(s)
```
`verify.sh` exits 0 and prints `OK: detector behaves as expected` when the compatible spec passes and the breaking one is caught. I ran `breaking.py` directly on both specs (0 and 6 findings); `verify.sh` itself was not executed.

## What it proves
- `orders-v2-compatible.json` (new optional query parameter, new response field, new endpoint) gives 0 findings.
- `orders-v2-breaking.json` is flagged for a parameter type change, a new required parameter, a removed and a retyped response field, a removed 404 response and a removed operation.
- The exit code 1 is what a CI gate needs: run it against the spec on the main branch.

## Trade-offs
- `breaking.py` is a teaching detector with a handful of rules, JSON specs only, and no `$ref` resolution, enums or request bodies. In a real pipeline use [oasdiff](https://github.com/oasdiff/oasdiff), which was not installed or run here.
- Spec checks see only the declared contract; they cannot tell which fields a consumer actually reads, which is what Pact adds.
- It checks the spec, not the implementation; pair it with a test that the service matches the spec.

## When not to use it
- There is no published OpenAPI spec, or it is not kept in sync with the code.
- A few internal consumers you control; consumer-driven contracts (`../pact-consumer`) give sharper signals.
