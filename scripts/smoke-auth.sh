#!/usr/bin/env bash
set -e

BASE="http://localhost:8081"
EMAIL="demo-$(date +%s)@example.com"
PASS="Password123!"

echo "== 1) Register =="
REG_JSON=$(curl -s -X POST "$BASE/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASS\"}")

echo "$REG_JSON"

ACCESS=$(echo "$REG_JSON" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
REFRESH=$(echo "$REG_JSON" | sed -n 's/.*"refreshToken":"\([^"]*\)".*/\1/p')

if [ -z "$ACCESS" ]; then
  echo "ERROR: accessToken not found"
  exit 1
fi

echo
echo "== 2) /v1/me (should be 200) =="
curl -i -s "$BASE/v1/me" \
  -H "Authorization: Bearer $ACCESS"

echo
echo "== 3) Refresh =="
REFRESH_JSON=$(curl -s -X POST "$BASE/v1/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH\"}")

echo "$REFRESH_JSON"

NEW_ACCESS=$(echo "$REFRESH_JSON" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
NEW_REFRESH=$(echo "$REFRESH_JSON" | sed -n 's/.*"refreshToken":"\([^"]*\)".*/\1/p')

echo
echo "== 4) /v1/me with NEW access token (should be 200) =="
curl -i -s "$BASE/v1/me" \
  -H "Authorization: Bearer $NEW_ACCESS"

echo
echo "== 5) Logout =="
curl -i -s -X POST "$BASE/v1/auth/logout" \
  -H "Authorization: Bearer $NEW_ACCESS" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$NEW_REFRESH\"}"

echo
echo "== 6) Refresh again (should FAIL) =="
curl -i -s -X POST "$BASE/v1/auth/refresh" \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$NEW_REFRESH\"}"

echo
echo "DONE -- yay!"
