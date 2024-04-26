# Generation of TypeScript types from data model

This project generates TypeScript types from the data model, based on the JSON schema files.

It produces a Node.js package, published to Node.js repository for consumption within your projects. See [instructions in the README](../../README.md) for details.

## Usage

Here is an example using the `IdentityCheckCredentialJWTClass` type.

```javascript
const credentials: IdentityCheckCredentialJWTClass = {
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
```

## Example

See the [example project](../../examples/typescript-nodejs) for an example of using the
model, then serialising it to JSON.

---

## Build and run

### Prerequisites

- Generate the schema files from this project (run `./scripts/generate_json_schemas.sh -o combined -j ./code-generators/typescript-types/schemas`)
- Ensure you have Node.js installed and have run `npm ci` to fetch dependencies

## Generate

Generate the TypeScript types:

```shell
npm run generate
```

The generated TypeScript types are written to the `dist` directory.

## Publish Node.js package

You can publish the generated types as a Node.js package.

> **Note**
> To set the version of the artifact, set the `version` property in `package.json`.
>
> For example:
> ```shell
> npm version 1.2.3
> ```

### Publish to GitHub Packages Node.js repository

To publish the artifact to the GitHub Packages Node.js repository, use the `publish` npm command.

Per the [GitHub documentation](https://docs.github.com/en/actions/publishing-packages/publishing-nodejs-packages) ensure the `.npmrc` file is configured and the following environment variables are set:

- `NODE_AUTH_TOKEN`

> **Note**
> For this to succeed, you must have the appropriate publishing permissions.

Example:

```shell
npm publish
```

### Publishing to another Node.js repository

This is out of the scope of this README. See the [official Node.js `npm-publish` documentation](https://docs.npmjs.com/cli/v10/commands/npm-publish).
