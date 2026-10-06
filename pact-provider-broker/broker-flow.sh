#!/usr/bin/env bash
# Publish the consumer pact, record the provider verification result, then ask can-i-deploy.
# Needs the broker from docker-compose.yml on :9292. Uses only curl and the broker's HTTP API.
set -euo pipefail
cd "$(dirname "$0")"
BROKER=${BROKER:-http://localhost:9292}
PACT=src/test/resources/pacts/orders-web-orders-api.json
VER=${VERSION:-1.0.0}

# 1. consumer publishes its pact for version $VER
curl -fsS -X PUT -H 'Content-Type: application/json' -d @"$PACT" \
  "$BROKER/pacts/provider/orders-api/consumer/orders-web/version/$VER" >/dev/null
curl -fsS -X PUT "$BROKER/pacticipants/orders-web/versions/$VER/tags/main" -H 'Content-Type: application/json' -d '{}' >/dev/null

# 2. provider verified the pact (mvn test passed); publish the result to the link the broker gave us
LINK=$(curl -fsS "$BROKER/pacts/provider/orders-api/consumer/orders-web/version/$VER" \
  | python3 -c 'import json,sys;print(json.load(sys.stdin)["_links"]["pb:publish-verification-results"]["href"])')
curl -fsS -X POST -H 'Content-Type: application/json' \
  -d "{\"success\":true,\"providerApplicationVersion\":\"$VER\"}" "$LINK" >/dev/null

# 3. can-i-deploy: the matrix says whether orders-web $VER is verified by the provider
curl -fsSg "$BROKER/matrix?q[][pacticipant]=orders-web&q[][version]=$VER&latestby=cvpv&latest=true" \
  | python3 -c 'import json,sys;d=json.load(sys.stdin);print("can-i-deploy:",d["summary"]["deployable"],"-",d["summary"]["reason"]);sys.exit(0 if d["summary"]["deployable"] else 1)'
