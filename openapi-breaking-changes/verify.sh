#!/usr/bin/env bash
# Expect: the compatible change passes (exit 0) and the breaking change is caught (exit 1).
set -u
cd "$(dirname "$0")"
python3 breaking.py orders-v1.json orders-v2-compatible.json || { echo "FAIL: compatible change flagged"; exit 1; }
python3 breaking.py orders-v1.json orders-v2-breaking.json && { echo "FAIL: breaking change missed"; exit 1; }
echo "OK: detector behaves as expected"
