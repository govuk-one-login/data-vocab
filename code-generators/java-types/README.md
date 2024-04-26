# Generation of Java POJOs from data model

This project generates data model classes as Java POJOs, based on the JSON schema files.

There are two artifacts produced:

- `di-data-model` - Java POJOs, without any annotations, produced by the `plain-pojo` module
- `di-data-model-jackson` - Java POJOs, annotated using Jackson, produced by the `jackson-annotated` module

The artifacts are published to a Maven repository for consumption within your projects. See [instructions in the README](../../README.md) for details.

## Usage

Here is an example using the `IdentityCheckCredentialJWT` class.

> **Note**
> In this example, we make use of the builder pattern (the static `builder()` method, and its
> associated `with...` methods).
> 
> If preferred, the POJO's constructor, and its setters and getters can be used to construct
> instances instead. 

```java
var credentials = IdentityCheckCredentialJWT.builder()
    .withSub("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce")
    .withAud("https://passport.core.stubs.account.gov.uk")
    .withNbf(1690816091)
    .withIss("https://review-p.build.account.gov.uk")
    .build();

var vc = IdentityCheckCredential().builder()
    .withType(List.of(
        VerifiableCredentialType.VERIFIABLE_CREDENTIAL,
        VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL
    )).build();

credentials.setVc(vc);

var evidence = IdentityCheck.builder()
    .withValidityScore(0)
    .withStrengthScore(4)
    .withCi(List.of("D02"))
    .withTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9")
    .withType("IdentityCheck")
    .build();

vc.setEvidence(List.of(evidence));
```

## Example

See the unit test `uk.gov.di.model.ModelTest` (under `src/test/java`) for an example of using the
model, and deserialising to it from JSON.

---

## Build and run

### Prerequisites

- Generate the schema files from this project (run `./scripts/generate_json_schemas.sh -o combined -j ./code-generators/java-types/schemas`)
- Ensure you have Java 17+ installed

## Generate

Generate the Java types and run the unit tests:

```shell
./gradlew build
```

The generated Java types are written to the `build/generated-sources` directory under their respective sub-modules.

## Publish Maven artifact

You can publish the generated types as a Maven artifact.

> **Note**
> To set the version of the artifact, set the `version` Gradle property.
>
> For example:
> ```shell
> ./gradlew <tasks here> -Pversion=0.1.1-SNAPSHOT
> ```

### Publish to local Maven repository

To publish the artifact to your local Maven cache, use the `publishToMavenLocal` Gradle task:

```shell
./gradlew clean publishToMavenLocal
```

You can see the artifact and its Maven metadata in the usual location:

```
$ tree ~/.m2/repository/uk/gov/di/model
└── di-data-model
    ├── 0.1.0-SNAPSHOT
    │   ├── di-data-model-0.1.0-SNAPSHOT.jar
    │   ├── di-data-model-0.1.0-SNAPSHOT.module
    │   └── di-data-model-0.1.0-SNAPSHOT.pom
    ├── 0.1.1-SNAPSHOT
    │   ├── di-data-model-0.1.1-SNAPSHOT.jar
    │   ├── di-data-model-0.1.1-SNAPSHOT.module
    │   ├── di-data-model-0.1.1-SNAPSHOT.pom
    │   └── maven-metadata-local.xml
    └── maven-metadata-local.xml
```

### Publish to GitHub Packages Maven repository

To publish the artifact to the GitHub Packages Maven repository, use the `publish` Gradle task.

Per the [GitHub documentation](https://docs.github.com/en/actions/publishing-packages/publishing-java-packages-with-gradle) ensure the following environment variables are set:

- `GITHUB_ACTOR`
- `GITHUB_TOKEN`

> **Note**
> For this to succeed, you must have the appropriate publishing permissions.

Example:

```shell
./gradlew clean publish
```

### Publishing to another Maven repository

This is out of the scope of this README. See the [official Gradle `maven-publish` documentation](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example).
