# pact-consumer

A Pact JVM consumer test for the `orders-web` client (`OrderClient.java`) that writes a pact file for `orders-api`.

## Goal
Show a consumer-driven contract: the consumer declares the responses it relies on, and the test run turns that into a pact file the provider can verify.

## Run it
```bash
mvn -q -B test
```
Expected: `OrderClientPactTest` runs 2 tests with no failures, and `target/pacts/orders-web-orders-api.json` is written.

Not re-run while writing this README (the Maven here is a Windows install without Java 21 in this shell); the last recorded surefire report showed 2 tests, 0 failures.

## What it proves
- `OrderClient` works against a Pact mock server built from the declared interactions, with no real provider running.
- Two interactions are recorded as a V4 pact: `GET /orders/42` (200, `id` integer, `status` matching `NEW|PAID|SHIPPED`) and `GET /orders/99` (404). Matchers are loose on values and strict on shape.
- The provider states `order 42 exists` and `order 99 does not exist` are what the provider must set up; see `../pact-provider-broker`.

## Trade-offs
- The pact covers only what this consumer reads, so it stays small, but it says nothing about fields nobody uses.
- One `@Pact` method per test keeps each mock server strict: every declared interaction must be called.
- `OrderClient` parses the JSON body with regexes to avoid extra dependencies; a real client would use Jackson.

## When not to use it
- One team owns both sides and deploys them together; a plain integration test is simpler.
- The provider has many unknown consumers (a public API); use spec-based checks, see `../openapi-breaking-changes`.
