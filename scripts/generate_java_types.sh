#!/usr/bin/env bash
set -e

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi

cd "${ROOT_DIR}/code-generators/java-types"
./gradlew clean build
