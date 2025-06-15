#!/bin/bash

source "get_token.sh"

if ! TOKEN=$(get_token); then
  echo "Aborting: Token not available"
  exit 1
fi

echo "Getting constellation 1 ..."

RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/constellations/1 | jq)

echo "Response:"
echo "$RESPONSE" | jq .
