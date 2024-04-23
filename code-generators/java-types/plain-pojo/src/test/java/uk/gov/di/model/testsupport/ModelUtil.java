package uk.gov.di.model.testsupport;

import uk.gov.di.model.*;

import java.util.List;

/**
 * Constructs identity check credential instances.
 */
public class ModelUtil {
    public static IdentityCheckCredentialJWT buildCredential() {
        return IdentityCheckCredentialJWT.builder()
                .withSub("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce")
                .withAud("https://passport.core.stubs.account.gov.uk")
                .withNbf(1690816091)
                .withIss("https://review-p.build.account.gov.uk")
                .withVc(buildVc())
                .build();
    }

    @SuppressWarnings("unchecked")
    private static IdentityCheckCredentialClass buildVc() {
        var evidence = IdentityCheckClass.builder()
                .withValidityScore(0)
                .withStrengthScore(4)
                .withCi(List.of("D02"))
                .withTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9")
                .withType(IdentityCheckClass.IdentityCheckType.IDENTITY_CHECK_)
                .build();

        return IdentityCheckCredentialClass.builder()
                .withCredentialSubject(buildCredentialSubject())
                .withEvidence(List.of(evidence))
                .withType(List.of(
                        VerifiableCredentialType.VERIFIABLE_CREDENTIAL,
                        VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL
                )).build();
    }

    @SuppressWarnings("unchecked")
    private static IdentityCheckSubjectClass buildCredentialSubject() {
        var birthDate = BirthDateClass.builder()
                .withValue("1990-01-23")
                .build();

        var name = NameClass.builder().withNameParts(List.of(
                NamePartClass.builder()
                        .withType(NamePartClass.NamePartType.GIVEN_NAME)
                        .withValue("Kenneth")
                        .build(),
                NamePartClass.builder()
                        .withType(NamePartClass.NamePartType.FAMILY_NAME)
                        .withValue("Decerqueira")
                        .build()
        )).build();

        var passport = PassportDetailsClass.builder()
                .withExpiryDate("2030-12-12")
                .withIcaoIssuerCode("GBR")
                .withDocumentNumber("123456789")
                .build();

        return IdentityCheckSubjectClass.builder()
                .withBirthDate(List.of(birthDate))
                .withName(List.of(name))
                .withPassport(List.of(passport))
                .build();
    }
}
