#!/usr/bin/env bash
set -e

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi

if [[ $# -lt 1 ]]; then
  echo "Missing release type. Must be one of major, minor or patch"
  exit 1
else
  RELEASE_TYPE="$1"
fi

cd "${ROOT_DIR}"

npm version "$RELEASE_TYPE" --git-tag-version=false

# update lockfile etc.
npm install

NEW_VERSION="$( npm pkg get version | sed 's/"//g' )"

echo -e "New version: ${NEW_VERSION}\nCommit changes and create tag (y/N)?"
read -r CONFIRM_COMMIT
if [[ -z "$CONFIRM_COMMIT" || "y" != "$CONFIRM_COMMIT" ]]; then
  echo "Aborted"
  exit 1
fi

git commit \
  package.json \
  package-lock.json \
  -m"build: release ${NEW_VERSION}."

git tag "v$NEW_VERSION"
