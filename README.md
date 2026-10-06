# test-contract-pact-scc

Five small contract-testing examples around a tiny Orders API: consumer-driven pacts, a Pact Broker, Spring Cloud Contract, OpenAPI breaking-change detection and an AsyncAPI message contract, so you can compare when each one fits.

## What is inside

| Folder | What it shows | Run |
| --- | --- | --- |
| [`pact-consumer`](./pact-consumer) | A consumer states what it needs from `orders-api` and Pact JVM writes the pact file | `mvn -q -B test` |
| [`pact-provider-broker`](./pact-provider-broker) | The provider verifies that pact; a compose Pact Broker answers `can-i-deploy` | `mvn -q -B test` |
| [`spring-cloud-contract`](./spring-cloud-contract) | Provider-written YAML contracts become generated tests and a stubs jar | `mvn -q -B verify` |
| [`openapi-breaking-changes`](./openapi-breaking-changes) | A small detector that flags breaking changes between two OpenAPI specs | `bash verify.sh` |
| [`asyncapi-contract`](./asyncapi-contract) | An `orders.created` event described in AsyncAPI and checked against its schema | `python3 check.py` |

## Prerequisites

- Java 21 and Maven (Pact JVM 4.6.14, JUnit 5.10.2, Spring Boot 3.3.4, Spring Cloud 2023.0.3, all pinned)
- Python 3 (standard library only) for the last two folders
- Docker with Compose, only for the broker part of `pact-provider-broker`

## How to read it

Start with `pact-consumer`, then `pact-provider-broker`, which verifies the file the first one produces. The other three folders are independent alternatives; each README says when to choose it over the others. Spec diffs (OpenAPI, AsyncAPI) are a cheap gate on every change, and Pact adds stronger guarantees for specific consumers.
