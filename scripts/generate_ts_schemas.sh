#!/usr/bin/env bash
set -e

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi
JSON_SCHEMA_DIR="${ROOT_DIR}/code-generators/typescript-schemas/schemas"

# Generate JSON Schemas to include in TypeScript package
cd "${ROOT_DIR}/scripts"
./generate_json_schemas.sh -j "$JSON_SCHEMA_DIR"

cd "${ROOT_DIR}/code-generators/typescript-schemas"
npm ci
npm run clean
npm run generate
