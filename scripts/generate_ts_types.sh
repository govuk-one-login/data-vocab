#!/usr/bin/env bash
set -e

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi
JSON_SCHEMA_DIR="${ROOT_DIR}/code-generators/typescript-types/schemas"

# Generate intermediate JSON Schema used for the TypeScript type generation
cd "${ROOT_DIR}/scripts"
./generate_json_schemas.sh -j "$JSON_SCHEMA_DIR" -o combined

cd "${ROOT_DIR}/code-generators/typescript-types"
npm ci
npm run clean
npm run generate
