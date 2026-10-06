#!/usr/bin/env python3
"""Tiny OpenAPI breaking-change detector (stdlib only). Usage: oasdiff.py OLD.json NEW.json
Exit 1 when NEW breaks clients of OLD. Covers a few common rules only; use oasdiff for real."""
import json, sys

def load(path):
    with open(path) as f:
        return json.load(f)

def props(schema):
    return schema.get("properties", {}), set(schema.get("required", []))

def diff_schema(old, new, where, out):
    """Response schema: clients read fields, so removing or retyping them breaks."""
    op, _ = props(old)
    np, _ = props(new)
    for name, s in op.items():
        if name not in np:
            out.append(f"{where}: response field '{name}' removed")
        elif s.get("type") != np[name].get("type"):
            out.append(f"{where}: response field '{name}' type {s.get('type')} -> {np[name].get('type')}")
    return out

def diff(old, new):
    out = []
    for path, ops in old["paths"].items():
        for method, o in ops.items():
            where = f"{method.upper()} {path}"
            n = new["paths"].get(path, {}).get(method)
            if n is None:
                out.append(f"{where}: operation removed")
                continue
            oparams = {(p["name"], p["in"]): p for p in o.get("parameters", [])}
            nparams = {(p["name"], p["in"]): p for p in n.get("parameters", [])}
            for key, p in nparams.items():
                if key not in oparams and p.get("required"):
                    out.append(f"{where}: new required {key[1]} parameter '{key[0]}'")
                elif key in oparams and p["schema"].get("type") != oparams[key]["schema"].get("type"):
                    out.append(f"{where}: parameter '{key[0]}' type changed")
            for code, r in o.get("responses", {}).items():
                if code not in n.get("responses", {}):
                    out.append(f"{where}: response {code} removed")
                    continue
                os_ = r.get("content", {}).get("application/json", {}).get("schema")
                ns_ = n["responses"][code].get("content", {}).get("application/json", {}).get("schema")
                if os_ and ns_:
                    diff_schema(os_, ns_, f"{where} {code}", out)
    return out

if __name__ == "__main__":
    problems = diff(load(sys.argv[1]), load(sys.argv[2]))
    for p in problems:
        print("BREAKING:", p)
    print(f"{len(problems)} breaking change(s)")
    sys.exit(1 if problems else 0)
