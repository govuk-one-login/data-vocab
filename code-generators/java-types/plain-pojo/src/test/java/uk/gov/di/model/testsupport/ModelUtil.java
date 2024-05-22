package uk.gov.di.model.testsupport;

import uk.gov.di.model.*;

import java.net.URI;
import java.util.List;

/**
 * Constructs identity check credential instances.
 */
public class ModelUtil {
    public static IdentityCheckCredentialJWT buildCredential() {
        var credentialBuilder = IdentityCheckCredentialJWT.builder();
        credentialBuilder
                .withVc(buildVc())
                .withSub(URI.create("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce"))
                .withAud(URI.create("https://passport.core.stubs.account.gov.uk"))
                .withNbf(1690816091)
                .withIss(URI.create("https://review-p.build.account.gov.uk"));
        return credentialBuilder.build();
    }

    @SuppressWarnings("unchecked")
    private static IdentityCheckCredential buildVc() {
        var evidence = IdentityCheck.builder()
                .withValidityScore(0)
                .withStrengthScore(4)
                .withCi(List.of("D02"))
                .withTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9")
                .withType(IdentityCheck.IdentityCheckType.IDENTITY_CHECK_)
                .build();

        return IdentityCheckCredential.builder().withEvidence(List.of(evidence))
                .withCredentialSubject(buildCredentialSubject())
                .withType(List.of(
                        VerifiableCredentialType.VERIFIABLE_CREDENTIAL,
                        VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL
                ))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static IdentityCheckSubject buildCredentialSubject() {
        var birthDate = BirthDate.builder()
                .withValue("1990-01-23")
                .build();

        var name = Name.builder().withNameParts(List.of(
                NamePart.builder()
                        .withType(NamePart.NamePartType.GIVEN_NAME)
                        .withValue("Kenneth")
                        .build(),
                NamePart.builder()
                        .withType(NamePart.NamePartType.FAMILY_NAME)
                        .withValue("Decerqueira")
                        .build()
        )).build();

        var passport = PassportDetails.builder()
                .withExpiryDate("2030-12-12")
                .withIcaoIssuerCode("GBR")
                .withDocumentNumber("123456789")
                .build();

        var subjectBuilder = IdentityCheckSubject.builder();
        subjectBuilder
                .withPassport(List.of(passport))
                .withBirthDate(List.of(birthDate))
                .withName(List.of(name));
        return subjectBuilder.build();
    }
}
