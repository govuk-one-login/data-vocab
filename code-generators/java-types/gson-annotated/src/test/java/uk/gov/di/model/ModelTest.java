package uk.gov.di.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Deserialises an example JSON file to an instance of the model.
 */
class ModelTest {
    private static String serialisedModel;

    private static Gson gson;

    @BeforeAll
    static void beforeAll() throws Exception {
        serialisedModel = Files.readString(Path.of(Objects.requireNonNull(ModelTest.class.getResource("/serialised_model.json")).toURI()));

        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Test
    void gsonDeserialisationTest() {
        IdentityCheckCredentialJWT credential = gson.fromJson(serialisedModel, IdentityCheckCredentialJWT.class);

        // root object
        assertEquals(URI.create("https://passport.core.stubs.account.gov.uk"), credential.getAud());
        assertEquals(URI.create("https://review-p.build.account.gov.uk"), credential.getIss());
        assertEquals(1690816091, credential.getNbf());
        assertEquals(URI.create("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce"), credential.getSub());

        // vc
        IdentityCheckCredential vc = credential.getVc();
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
                hasProperty("type", equalTo(IdentityCheck.IdentityCheckType.IDENTITY_CHECK_)),
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
                        hasProperty("type", equalTo(NamePart.NamePartType.GIVEN_NAME)),
                        hasProperty("value", equalTo("Kenneth"))
                ),
                allOf(
                        hasProperty("type", equalTo(NamePart.NamePartType.FAMILY_NAME)),
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

    @Test
    void gsonSerialisationTest() {
        IdentityCheckCredentialJWT jwt = IdentityCheckCredentialJWT.builder()
                .withAud(URI.create("https://passport.core.stubs.account.gov.uk"))
                .withIss(URI.create("https://review-p.build.account.gov.uk"))
                .withNbf(1690816091)
                .withSub(URI.create("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce"))
                .withVc(IdentityCheckCredential.builder()
                        .withType(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL, VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL))
                        .withCredentialSubject(IdentityCheckSubject.builder()
                                .withBirthDate(List.of(BirthDate.builder()
                                                .withValue("1990-01-23")
                                        .build()))
                                .withName(List.of(Name.builder()
                                                .withNameParts(List.of(
                                                        NamePart.builder().withType(NamePart.NamePartType.GIVEN_NAME).withValue("Kenneth").build(),
                                                        NamePart.builder().withType(NamePart.NamePartType.FAMILY_NAME).withValue("Decerqueira").build()
                                                        ))
                                        .build()))
                                .withPassport(List.of(
                                        PassportDetails.builder()
                                                .withDocumentNumber("123456789")
                                                .withExpiryDate("2030-12-12")
                                                .withIcaoIssuerCode("GBR")
                                                .build()
                                ))
                                .build())
                        .withEvidence(List.of(
                                IdentityCheck.builder()
                                        .withType(IdentityCheck.IdentityCheckType.IDENTITY_CHECK_)
                                        .withCi(List.of("D02"))
                                        .withTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9")
                                        .withStrengthScore(4)
                                        .withValidityScore(0)
                                        .build()
                        ))
                        .build())
                .build();

        String serialiseData = gson.toJson(jwt);
        JsonElement parsedSerialisedData = JsonParser.parseString(serialiseData);
        JsonElement parsedExpectedData = JsonParser.parseString(serialisedModel);

        assertEquals(parsedExpectedData, parsedSerialisedData);
    }
}
