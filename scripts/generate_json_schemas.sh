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
  "credentials.yaml,AddressCredentialJWTClass,AddressCredentialJWT.json"
  "credentials.yaml,AuthorizationRequestClass,AuthorizationRequest.json"
  "credentials.yaml,CoreIdentityJWTClass,CoreIdentityJWT.json"
  "lifeEvents.yaml,DeathRegisteredJWTClass,DeathRegisteredJWT.json"
  "lifeEvents.yaml,DeathRegistrationUpdatedJWTClass,DeathRegistrationUpdatedJWT.json"
  "document.yaml,DrivingPermitDetailsClass,DrivingPermit.json"
  "document.yaml,IdCardDetailsClass,IdCard.json"
  "credentials.yaml,IdentityAssertionCredentialJWTClass,IdentityAssertionCredentialJWT.json"
  "identityCheckCredential.yaml,IdentityCheckCredentialClass,IdentityCheckCredential.json"
  "credentials.yaml,IdentityCheckCredentialJWTClass,IdentityCheckCredentialJWT.json"
  "credentials.yaml,InheritedIdentityJWTClass,InheritedIdentityJWT.json"
  "credentials.yaml,IssuerAuthorizationRequestClass,IssuerAuthorizationRequest.json"
  "name.yaml,NameClass,Name.json"
  "credentials.yaml,OpenIDConnectAuthenticationRequestClass,OpenIDConnectAuthenticationRequest.json"
  "document.yaml,PassportDetailsClass,PassportDetails.json"
  "address.yaml,PostalAddressClass,PostalAddress.json"
  "document.yaml,ResidencePermitDetailsClass,ResidencePermit.json"
  "riskAssessmentCredential.yaml,RiskAssessmentCredentialClass,RiskAssessmentCredential.json"
  "credentials.yaml,RiskAssessmentCredentialJWTClass,RiskAssessmentCredentialJWT.json"
  "securityCheckCredential.yaml,SecurityCheckCredentialClass,SecurityCheckCredential.json"
  "credentials.yaml,SecurityCheckCredentialJWTClass,SecurityCheckCredentialJWT.json"
  "document.yaml,SocialSecurityRecordDetailsClass,SocialSecurityRecord.json"
  "document.yaml,BankAccountDetailsClass,BankAccount.json"
)

if [[ -z "$ROOT_DIR" ]]; then
  ROOT_DIR="$( git rev-parse --show-toplevel )"
fi

# default LinkML input dir
# override with -l option
LINKML_SCHEMA_DIR="${ROOT_DIR}/v1/linkml-schemas"

# default JSON Schema output dir
# override with -j option
JSON_SCHEMA_DIR="${ROOT_DIR}/v1/json-schemas"

# Set to 'separate' or 'combined' using the -o option,
# to control whether multiple or combined JSON Schema
# files are produced.
OUTPUT_MODE="separate"
while getopts "j:o:l:" opt; do
  case ${opt} in
    j )
      JSON_SCHEMA_DIR=$OPTARG
      ;;
    l )
      LINKML_SCHEMA_DIR=$OPTARG
      ;;
    o )
      OUTPUT_MODE=$OPTARG
      ;;
    \? )
      echo "Invalid option: $OPTARG" 1>&2
      ;;
    : )
      echo "Invalid option: $OPTARG requires an argument" 1>&2
      ;;
  esac
done
shift $((OPTIND -1))

function prep_output_dir() {
  if [[ -d "${JSON_SCHEMA_DIR}" ]]; then
    rm -f "${JSON_SCHEMA_DIR}/*.json"
  else
    mkdir -p "${JSON_SCHEMA_DIR}"
  fi
}

#
# Writes separate JSON Schema files.
#
function generate_separate_files() {
  [[ -f $JSON_SCHEMA_DIR/index.md.template ]] && cp $JSON_SCHEMA_DIR/index.md.template $JSON_SCHEMA_DIR/index.md

  for LINKML_ITEM in "${LINKML_ITEMS[@]}"; do
    ITEM_DETAILS=(${LINKML_ITEM//,/ })
    LINKML_SCHEMA="${ITEM_DETAILS[0]}"
    LINKML_CLASS="${ITEM_DETAILS[1]}"
    JSON_SCHEMA="${ITEM_DETAILS[2]}"
    echo -e "\nWriting JSON schema for ${LINKML_CLASS} to ${JSON_SCHEMA_DIR}/${JSON_SCHEMA}"

    poetry run gen-json-schema --closed --no-metadata -t "${LINKML_CLASS}" \
      "${LINKML_SCHEMA_DIR}/${LINKML_SCHEMA}" > "${JSON_SCHEMA_DIR}/${JSON_SCHEMA}"
    [[ -f $JSON_SCHEMA_DIR/index.md ]] && echo "| [$JSON_SCHEMA]($JSON_SCHEMA) | [$LINKML_CLASS](../classes/$LINKML_CLASS) |" >> $JSON_SCHEMA_DIR/index.md
    poetry run python ${ROOT_DIR}/scripts/check_schema_see_also.py  "${LINKML_SCHEMA_DIR}/${LINKML_SCHEMA}" $LINKML_CLASS ../json-schemas/$JSON_SCHEMA
  done
}

#
# Writes a combined JSON Schema file.
#
function generate_combined_file() {
    poetry run gen-json-schema \
           --closed \
           --no-metadata \
           --nonstandard-extends \
           "${LINKML_SCHEMA_DIR}/credentials.yaml" > "${JSON_SCHEMA_DIR}/credentials.json"

    echo -e "\nWrote combined JSON schema to ${JSON_SCHEMA_DIR}/credentials.json"
}

prep_output_dir

echo "Using output mode: ${OUTPUT_MODE}"
case "$OUTPUT_MODE" in
  separate)
    generate_separate_files
    ;;
  combined)
    generate_combined_file
    ;;
  *)
    echo "Unsupported output mode: $OUTPUT_MODE - must be 'combined' or 'separate'"
    ;;
esac
