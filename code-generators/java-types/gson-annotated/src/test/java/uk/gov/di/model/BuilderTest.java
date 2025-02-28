package uk.gov.di.model;

import org.junit.jupiter.api.Test;

import java.util.List;

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
    void riskAssessmentCredentialReturnsCorrectType() {
        IdentityCheckCredential credential = IdentityCheckCredential.builder()
                .withType(List.of(VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL))
                .withEvidence(List.of())
                .withContext(List.of())
                .build();
        assertInstanceOf(IdentityCheckCredential.class, credential);
    }
}
