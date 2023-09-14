import {Credentials} from "@alphagov/di-identity-vocab/IdentityCheckCredentialJWT";

const credentials: Credentials = {
    sub: "urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce",
    aud: "https://passport.core.stubs.account.gov.uk",
    nbf: 1690816091,
    iss: "https://review-p.build.account.gov.uk",
    vc: {
        evidence: [{
            validityScore: 0,
            strengthScore: 4,
            ci: [
                "D02"
            ],
            txn: "5f57a8f2-62b0-4958-9332-06d9f453e5b9",
            type: "IdentityCheck"
        }],
        credentialSubject: {
            passport: [{
                expiryDate: "2030-12-12",
                icaoIssuerCode: "GBR",
                documentNumber: "123456789"
            }],
            name: [{
                nameParts: [{
                    type: "GivenName",
                    value: "Kenneth"
                },
                    {
                        type: "FamilyName",
                        value: "Decerqueira"
                    }
                ]
            }],
            birthDate: [{
                value: "1990-01-23"
            }]
        },
        type: [
            "VerifiableCredential",
            "IdentityCheckCredential"
        ]
    }
}

console.log(JSON.stringify(credentials));
