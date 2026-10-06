# spring-cloud-contract

A Spring Boot `orders-api` (`OrdersApp.java`) with two YAML contracts that Spring Cloud Contract turns into provider tests and a stubs jar.

## Goal
Show provider-driven contracts: the provider team writes the contract, the plugin generates tests that keep the controller honest, and consumers get stubs built from the same files.

## Run it
```bash
mvn -q -B verify
```
Expected: the plugin generates `lab.ContractVerifierTest` with 2 tests, they pass, and `target/` gets an `orders-api-scc-1.0.0-stubs.jar`. The first run downloads Spring Boot 3.3.4 and Spring Cloud 2023.0.3.

Not run end to end: this command was not run while writing this README (no Java 21 Maven in this shell), so the expected output is what the configuration is set up to produce.

## What it proves
- `src/test/resources/contracts/shouldReturnOrder.yml` (200, `id` 42, `status` matching `NEW|PAID|SHIPPED`) and `shouldReturn404.yml` become MockMvc tests that run against the real `OrderController` through `BaseContractTest.java`.
- Renaming `status` in `Order` would fail the generated test, so the provider cannot drift from its published contract.
- The same contracts are packaged as a WireMock stubs jar for consumers; no consumer using it is included.

## Trade-offs
- The provider owns the contract, so publishing is simple, but consumers get no say and there is no `can-i-deploy` equivalent.
- The generated tests are only as good as the base class: here a standalone MockMvc setup with no database or security.
- Best when provider and consumers are all on the JVM and Spring, although the stubs themselves are plain WireMock.

## When not to use it
- Consumers are not JVM and their needs should drive the API; use Pact (`../pact-consumer`).
- A public API whose OpenAPI spec is the source of truth; use `../openapi-breaking-changes`.
