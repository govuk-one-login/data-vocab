#!/usr/bin/env bash
set -e

ROOT_DIR="$( git rev-parse --show-toplevel )"

cd "${ROOT_DIR}/code-generators/java-types"
./gradlew clean build
