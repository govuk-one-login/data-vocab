#!/usr/bin/env bash
set -e

ROOT_DIR="$( git rev-parse --show-toplevel )"

cd "${ROOT_DIR}/type-generator"
npm ci
npm run generate

TYPES_ARCHIVE="${ROOT_DIR}/v1/typescript-types.zip"
cd "${ROOT_DIR}/v1/types"
zip -r "${TYPES_ARCHIVE}" .

echo "Archived types to ${TYPES_ARCHIVE}"
