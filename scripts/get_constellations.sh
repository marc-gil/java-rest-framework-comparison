#!/bin/bash

source "get_token.sh"

if ! TOKEN=$(get_token); then
  echo "Aborting: Token not available"
  exit 1
fi

echo "Getting all constellations..."

RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/constellations | jq)

echo "Response:"
echo "$RESPONSE" | jq .
