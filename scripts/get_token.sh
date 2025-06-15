#!/bin/bash

get_token() {
  local USERNAME=${1:-test-user}
  local PASSWORD=${2:-test-password}

  echo "Logging in as $USERNAME..." >&2

  local RESPONSE
  RESPONSE=$(curl -s -X POST http://localhost:8081/login \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d 'username='"$USERNAME"'&password='"$PASSWORD")

  local TOKEN
  TOKEN=$(echo "$RESPONSE" | jq -r .access_token)

  if [[ "$TOKEN" == "null" || -z "$TOKEN" ]]; then
    echo "Failed to retrieve token. Response:" >&2
    echo "$RESPONSE" >&2
    return 1
  fi

  echo "Token retrieved successfully" >&2
  echo "$TOKEN"
}
