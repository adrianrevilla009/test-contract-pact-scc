# asyncapi-contract

An AsyncAPI 3.0 document (`asyncapi.json`) for the `orders.created` event and a stdlib Python checker (`check.py`) for its payload schema.

## Goal
Treat an event as a contract: describe the `OrderCreated` message in AsyncAPI and check producer payloads against its schema.

## Run it
```bash
python3 check.py
```
Expected, exit code 0:
```
valid   {'orderId': 42, 'status': 'NEW', 'total': 9.5} []
invalid {'orderId': 1, 'status': 'NEW'} ['$.total: required']
v2 (adds required 'currency') rejects v1 messages: True
OK
```
(The full run prints two valid and three invalid messages before the v2 line.)

## What it proves
- `asyncapi.json` holds the channel, the `send` operation and the JSON Schema payload of `OrderCreated`.
- `check.py` accepts two valid messages and rejects three bad ones: wrong type, enum violation, missing required field.
- A producer change that adds a required field (`currency`) makes every old message invalid, the event equivalent of a breaking API change; add fields as optional instead.

## Trade-offs
- The validator is a small subset of JSON Schema (type, enum, required, additionalProperties). Real projects validate the document with the AsyncAPI CLI and use a full JSON Schema library or a schema registry; neither was run here.
- Only the payload is checked, not delivery, ordering, headers or topic configuration.
- `additionalProperties: false` makes the contract strict, so consumers on the old schema reject new optional fields.

## When not to use it
- One producer and one consumer in a single codebase that share a typed class.
- Teams that already enforce compatibility with a schema registry; the registry is the contract there.
