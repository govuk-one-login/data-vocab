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

#
# Update root schemas project.
#
function update_root_project(){
  cd "${ROOT_DIR}"

  npm version "$RELEASE_TYPE" --git-tag-version=false

  # update lockfile etc.
  npm install
}

#
# Update TypeScript type generator project.
#
function update_ts_generator() {
  cd "${ROOT_DIR}/code-generators/typescript-types"

  npm version "$1"

  # update lockfile etc.
  npm install
}

#
# Update Java type generator project.
#
function update_java_generator() {
  echo "version=$1" > "${ROOT_DIR}/code-generators/java-types/gradle.properties"
}

#
# Commits the changes to git and tags it.
#
function commit_and_tag() {
  local NEW_VERSION="$1"
  cd "${ROOT_DIR}"

  git add \
    package.json \
    package-lock.json \
    code-generators/java-types/gradle.properties \
    code-generators/typescript-types/package.json \
    code-generators/typescript-types/package-lock.json

  git commit -m"build: release ${NEW_VERSION}."
  git tag "v$NEW_VERSION"
}

update_root_project

cd "${ROOT_DIR}"
NEW_VERSION="$( npm pkg get version | sed 's/"//g' )"

update_ts_generator "${NEW_VERSION}"
update_java_generator "${NEW_VERSION}"

echo -e "New version: ${NEW_VERSION}\nCommit changes and create tag (y/N)?"
read -r CONFIRM_COMMIT
if [[ -z "$CONFIRM_COMMIT" || "y" != "$CONFIRM_COMMIT" ]]; then
  echo "Aborted"
  exit 1
fi

commit_and_tag "${NEW_VERSION}"
