#!/usr/bin/env bash
set -e

ROOT_DIR="$( git rev-parse --show-toplevel )"
LINKML_SCHEMA_DIR="${ROOT_DIR}/v1/linkml-schemas"
JSON_SCHEMA_DIR="${ROOT_DIR}/code-generators/java-types/schemas"

# Generate intermediate json-schema used for the Java type generation
mkdir -p $JSON_SCHEMA_DIR
poetry run gen-json-schema --closed --no-metadata --nonstandard-extends "${LINKML_SCHEMA_DIR}/credentials.yaml" > "${JSON_SCHEMA_DIR}/credentials.json"

cd "${ROOT_DIR}/code-generators/java-types"
./gradlew clean build
