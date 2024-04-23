package uk.gov.di.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Deserialises an example JSON file to an instance of the model.
 */
class ModelTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static File serialisedModel;

    @BeforeAll
    static void beforeAll() throws Exception {
        serialisedModel = Path.of(ModelTest.class.getResource("/serialised_model.json").toURI()).toFile();
    }

    @Test
    public void jsonShouldDeserialiseToModel() throws Exception {
        var credentials = OBJECT_MAPPER.readValue(serialisedModel, IdentityCheckCredentialJWT.class);

        // root object
        assertEquals("https://passport.core.stubs.account.gov.uk", credentials.getAud());
        assertEquals("https://review-p.build.account.gov.uk", credentials.getIss());
        assertEquals(1690816091, credentials.getNbf());
        assertEquals("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce", credentials.getSub());

        // vc
        var vc = credentials.getVc();
        assertNotNull(vc);
        assertThat(vc.getType(), hasItems(
                equalTo(VerifiableCredentialType.VERIFIABLE_CREDENTIAL),
                equalTo(VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL)
        ));

        // evidence
        var evidence = vc.getEvidence();
        assertThat(evidence, hasSize(1));
        assertThat(evidence, hasItem(allOf(
                hasProperty("ci", hasItem(equalTo("D02"))),
                hasProperty("strengthScore", equalTo(4)),
                hasProperty("txn", equalTo("5f57a8f2-62b0-4958-9332-06d9f453e5b9")),
                hasProperty("type", equalTo(IdentityCheckClass.IdentityCheckType.IDENTITY_CHECK_)),
                hasProperty("validityScore", equalTo(0))
        )));

        // subject
        var credentialSubject = vc.getCredentialSubject();
        assertNotNull(credentialSubject);
        assertThat(credentialSubject.getBirthDate(), hasSize(1));
        assertEquals("1990-01-23", credentialSubject.getBirthDate().get(0).getValue());

        // name
        var name = credentialSubject.getName();
        assertThat(name, hasSize(1));
        var nameParts = name.get(0).getNameParts();
        assertThat(nameParts, hasItems(
                allOf(
                        hasProperty("type", equalTo(NamePartClass.NamePartType.GIVEN_NAME)),
                        hasProperty("value", equalTo("Kenneth"))
                ),
                allOf(
                        hasProperty("type", equalTo(NamePartClass.NamePartType.FAMILY_NAME)),
                        hasProperty("value", equalTo("Decerqueira"))
                ))
        );

        // passports
        var passports = credentialSubject.getPassport();
        assertNotNull(passports);
        assertThat(passports, hasSize(1));
        assertThat(passports, hasItem(allOf(
                hasProperty("documentNumber", equalTo("123456789")),
                hasProperty("expiryDate", equalTo("2030-12-12")),
                hasProperty("icaoIssuerCode", equalTo("GBR"))
        )));
    }
}
