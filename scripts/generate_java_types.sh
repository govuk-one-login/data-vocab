#!/usr/bin/env bash
set -e

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi
JSON_SCHEMA_DIR="${ROOT_DIR}/code-generators/java-types/schemas"

# Generate intermediate json-schema used for the Java type generation
cd "${ROOT_DIR}/scripts"
./generate_json_schemas.sh -j "${JSON_SCHEMA_DIR}/combined" -o combined

# Generate all json-schema used for the Java schema package generation
./generate_json_schemas.sh -j "${JSON_SCHEMA_DIR}/separate"

cd "${ROOT_DIR}/code-generators/java-types"
./gradlew clean build
