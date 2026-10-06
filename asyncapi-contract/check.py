#!/usr/bin/env python3
"""Message contract check (stdlib only): validate sample messages against the AsyncAPI payload schema,
and show why a producer change that adds a required field breaks existing consumers' data."""
import json, sys
from pathlib import Path

HERE = Path(__file__).parent
TYPES = {"object": dict, "string": str, "integer": int, "number": (int, float), "boolean": bool}

def validate(value, schema, path="$"):
    errs = []
    t = schema.get("type")
    if t and (not isinstance(value, TYPES[t]) or (t in ("integer", "number") and isinstance(value, bool))):
        return [f"{path}: expected {t}"]
    if "enum" in schema and value not in schema["enum"]:
        errs.append(f"{path}: {value!r} not in {schema['enum']}")
    if t == "object":
        props = schema.get("properties", {})
        errs += [f"{path}.{r}: required" for r in schema.get("required", []) if r not in value]
        for k, v in value.items():
            if k in props:
                errs += validate(v, props[k], f"{path}.{k}")
            elif schema.get("additionalProperties") is False:
                errs.append(f"{path}.{k}: unexpected property")
    return errs

def main():
    spec = json.loads((HERE / "asyncapi.json").read_text())
    schema = spec["components"]["messages"]["OrderCreated"]["payload"]
    good = [{"orderId": 42, "status": "NEW", "total": 9.5}, {"orderId": 7, "status": "PAID", "total": 10, "note": "gift"}]
    bad = [{"orderId": "42", "status": "NEW", "total": 9.5}, {"orderId": 1, "status": "LOST", "total": 1}, {"orderId": 1, "status": "NEW"}]
    ok = True
    for m in good:
        e = validate(m, schema)
        print("valid  " if not e else "WRONG  ", m, e)
        ok &= not e
    for m in bad:
        e = validate(m, schema)
        print("invalid", m, e)
        ok &= bool(e)
    # producer v2 adds a required field: messages from the old producer no longer satisfy the contract
    v2 = json.loads(json.dumps(schema))
    v2["required"].append("currency")
    v2["properties"]["currency"] = {"type": "string"}
    broke = [validate(m, v2) for m in good]
    print("v2 (adds required 'currency') rejects v1 messages:", all(broke))
    ok &= all(broke)
    print("OK" if ok else "FAIL")
    sys.exit(0 if ok else 1)

if __name__ == "__main__":
    main()
