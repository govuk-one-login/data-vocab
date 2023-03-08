---
homepage: true
layout: page
title: GOV.UK Vocabulary
---

# GOV.UK One Login vocabulary

## Introduction

This vocabulary covers a number of use cases in the One Login domain, including:

* identity proofing and verification to UK government standards such as [Good Practice Guide (GPG) 45](https://www.gov.uk/government/publications/identity-proofing-and-verification-of-an-individual)
* verifiable credential issuance, in particular of [identity check credentials](v1/IdentityCheckCredentialClass)
* [identity presentation](v1/CoreIdentityJWTClass)
* OAuth 2.0 [authorization requests](v1/AuthorizationRequestClass) to credential issuing and credential collecting services

In future it may be extended to cover OpenID Connect authentication requests and security events.

## Schemas

* [IdentityCheckCredential](v1/json-schemas/IdentityCheckCredential.json)
* [IdentityCheckCredentialJWT](v1/json-schemas/IdentityCheckCredentialJWT.json)
* [CoreIdentityJWT](v1/json-schemas/CoreIdentityJWT.json)
* [AuthorizationRequest](v1/json-schemas/AuthorizationRequest.json)
* [Name](v1/json-schemas/Name.json)
* [PostalAddress](v1/json-schemas/PostalAddress.json)
* [DrivingPermit](v1/json-schemas/DrivingPermit.json)
* [Passport](v1/json-schemas/PassportDetails.json)
* [ResidencePermit](v1/json-schemas/ResidencePermit.json)

### Examples

* [Name Examples](v1/NameClass)
* [Postal Address Examples](v1/PostalAddressClass)
* [Passport Details Examples](v1/PassportDetailsClass)
* [Driving Permit Details Examples](v1/DrivingPermitDetailsClass)
* [Residence Permit Details Examples](v1/ResidencePermitDetailsClass)
