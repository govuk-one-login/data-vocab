#!/usr/bin/env bash
set -e

#
# Generates JSON Schema files from LinkML schemas.
#
# Prerequisites:
# - Python 3
# - Poetry
#

# format: <linkml schema file>,<linkml class>,<json schema file>
LINKML_ITEMS=(
  "credentials.yaml,CoreIdentityJWTClass,CoreIdentityJWT.json"
  "credentials.yaml,IdentityCheckCredentialJWTClass,IdentityCheckCredentialJWT.json"
  "credentials.yaml,IdentityCheckCredentialClass,IdentityCheckCredential.json"
  "credentials.yaml,AuthorizationRequestClass,AuthorizationRequest.json"
  "credentials.yaml,SecurityCheckCredentialClass,SecurityCheckCredential.json"
  "address.yaml,PostalAddressClass,PostalAddress.json"
  "document.yaml,PassportDetailsClass,PassportDetails.json"
  "document.yaml,DrivingPermitDetailsClass,DrivingPermit.json"
  "document.yaml,ResidencePermitDetailsClass,ResidencePermit.json"
  "document.yaml,SocialSecurityRecordDetailsClass,SocialSecurityRecord.json"
  "document.yaml,IdCardDetailsClass,IdCard.json"
  "name.yaml,NameClass,Name.json"
  "credentials.yaml,IssuerAuthorizationRequestClass,IssuerAuthorizationRequest.json"
  "credentials.yaml,OpenIDConnectAuthenticationRequestClass,OpenIDConnectAuthenticationRequest.json"
)

ROOT_DIR="$( git rev-parse --show-toplevel )"
JSON_SCHEMA_DIR="${ROOT_DIR}/v1/json-schemas"
LINKML_SCHEMA_DIR="${ROOT_DIR}/v1/linkml-schemas"

rm -f "${JSON_SCHEMA_DIR}/*.json"

for LINKML_ITEM in "${LINKML_ITEMS[@]}"; do
  ITEM_DETAILS=(${LINKML_ITEM//,/ })
  LINKML_SCHEMA="${ITEM_DETAILS[0]}"
  LINKML_CLASS="${ITEM_DETAILS[1]}"
  JSON_SCHEMA="${ITEM_DETAILS[2]}"
  echo -e "\nWriting JSON schema for ${LINKML_CLASS} to ${JSON_SCHEMA}"

  poetry run gen-json-schema --closed --no-metadata -t "${LINKML_CLASS}" \
    "${LINKML_SCHEMA_DIR}/${LINKML_SCHEMA}" > "${JSON_SCHEMA_DIR}/${JSON_SCHEMA}"
done
