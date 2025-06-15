#!/bin/bash

source "get_token.sh"

if ! TOKEN=$(get_token); then
  echo "Aborting: Token not available"
  exit 1
fi

echo "Creating Cassiopeia constellation ..."

RESPONSE=$(curl -s -X POST http://localhost:8081/constellations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Cassiopeia\", \"hemisphere\":\"Northern\", \"description\": \"It has the form of a W or an M depending on it's position\"}" \
  | jq)

echo "Response:"
echo "$RESPONSE" | jq .

ID=$(echo "$RESPONSE" | jq .id)

echo "Getting recently created constellation with id $ID..."

RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/constellations/"$ID" | jq)

echo "Response:"
echo "$RESPONSE" | jq .
