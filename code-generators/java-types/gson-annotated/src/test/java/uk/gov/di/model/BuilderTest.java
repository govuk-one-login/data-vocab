package uk.gov.di.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BuilderTest {
    @Test
    void testBuildersReturnCorrectType() {
        assertInstanceOf(AddressCredential.class, AddressCredential.builder().build());
        assertInstanceOf(IdentityAssertionCredential.class, IdentityAssertionCredential.builder().build());
        assertInstanceOf(IdentityCheckCredential.class, IdentityCheckCredential.builder().build());
        assertInstanceOf(RiskAssessmentCredential.class, RiskAssessmentCredential.builder().build());
        assertInstanceOf(SecurityCheckCredential.class, SecurityCheckCredential.builder().build());
        assertInstanceOf(VerifiableCredential.class, VerifiableCredential.builder().build());
    }

    @Test
    void basicTypeCheckDetailsShouldBeConstructed() {
        CheckDetails checkDetails = CheckDetails.builder()
                .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK)
                .build();

        assertEquals(new CheckDetails() {
            {
                checkMethod = CheckDetails.CheckMethodType.DATA;
                dataCheck = DataCheckType.RECORD_CHECK;
            }
        }, checkDetails);
    }

    @Test
    void basicTypeNamePartShouldBeConstructed() {
        NamePart namePart = NamePart.builder().withValue("Alice").withType(NamePart.NamePartType.GIVEN_NAME).build();

        assertEquals(new NamePart() {
            {
                value = "Alice";
                type = NamePart.NamePartType.GIVEN_NAME;
            }
        }, namePart);
    }

    @Test
    void complexTypeEvidenceShouldBeConstructed() {
        IdentityCheck identityCheck = IdentityCheck.builder()
                .withType(IdentityCheck.IdentityCheckType.IDENTITY_CHECK_)
                .withTxn("abc1234")
                .withStrengthScore(4)
                .withValidityScore(2)
                .withCheckDetails(List.of(
                        CheckDetails.builder()
                                .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build(),
                        CheckDetails.builder()
                                .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build()
                ))
                .build();

        assertEquals(new IdentityCheck() {
            {
                type = IdentityCheckType.IDENTITY_CHECK_;
                txn = "abc1234";
                strengthScore = 4;
                validityScore = 2;
                checkDetails = List.of(
                        CheckDetails.builder()
                                .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build(),
                        CheckDetails.builder()
                                .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build()
                );
            } }, identityCheck);
    }

    @Test
    void riskIdentityCheckCredentialReturnsCorrectType() {
        IdentityCheckCredential credential = IdentityCheckCredential.builder()
                .withType(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL, VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL))
                .withEvidence(List.of(IdentityCheck.builder()
                        .withType(IdentityCheck.IdentityCheckType.IDENTITY_CHECK_)
                        .withTxn("abc1234")
                        .withStrengthScore(4)
                        .withValidityScore(2)
                        .withCheckDetails(List.of(
                                CheckDetails.builder()
                                        .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                        .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build(),
                                CheckDetails.builder()
                                        .withCheckMethod(CheckDetails.CheckMethodType.DATA)
                                        .withDataCheck(CheckDetails.DataCheckType.RECORD_CHECK).build()
                        ))
                        .build()))
                .withCredentialSubject(
                        IdentityCheckSubject.builder()
                                .withName(List.of(Name.builder()
                                        .withNameParts(List.of(
                                                NamePart.builder().withValue("Alice").withType(NamePart.NamePartType.GIVEN_NAME).build(),
                                                NamePart.builder().withValue("Jane").withType(NamePart.NamePartType.GIVEN_NAME).build(),
                                                NamePart.builder().withValue("Laura").withType(NamePart.NamePartType.GIVEN_NAME).build(),
                                                NamePart.builder().withValue("Doe").withType(NamePart.NamePartType.FAMILY_NAME).build()
                                        ))
                                        .build()))
                                .withBirthDate(List.of(BirthDate.builder()
                                        .withValue("1970-01-01")
                                        .build()))
                                .withPassport(List.of(PassportDetails.builder()
                                        .withDocumentNumber("122345678")
                                        .withExpiryDate("2022-02-02")
                                        .withIcaoIssuerCode("GBR")
                                        .build()))
                                .build()
                )
                .build();
        assertInstanceOf(IdentityCheckCredential.class, credential);
    }
}
