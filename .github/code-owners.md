---
homepage: true
layout: page
title: GOV.UK Vocabulary
---

# GOV.UK One Login vocabulary

## Introduction

This vocabulary covers a number of use cases in the GOV.UK One Login domain, including:

* OpenID Connect [authentication requests](v1/classes/OpenIDConnectAuthenticationRequestClass) from relying party services
* [Verifiable Credentials](https://www.w3.org/TR/vc-data-model/) (VCs) for [identity presentation](v1/classes/VerifiableIdentityCredentialClass) to relying parties
* OAuth 2.0 [authorization requests](v1/classes/IssuerAuthorizationRequestClass) to credential issuing and credential collecting services
* VCs for [identity proofing and verification](v1/classes/IdentityCheckCredentialClass)
* VCs for [other security checks](v1/classes/SecurityCheckCredentialClass)

In future it may be extended to cover security events.

We provide [JSON Schemas](./v1/json-schemas/) for many of the important classes in the vocabulary.

## Examples

* [Name Examples](v1/classes/NameClass)
* [Postal Address Examples](v1/classes/PostalAddressClass)
* [Passport Details Examples](v1/classes/PassportDetailsClass)
* [Driving Permit Details Examples](v1/classes/DrivingPermitDetailsClass)
* [Residence Permit Details Examples](v1/classes/ResidencePermitDetailsClass)
