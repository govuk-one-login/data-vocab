package uk.gov.di.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Builds an instance of the model, serialises it, then compares
 * the serialised version to an exemplar JSON string.
 */
class ModelTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static String serialisedModel;

    @BeforeAll
    static void beforeAll() throws Exception {
        var serialisedExample = Path.of(ModelTest.class.getResource("/serialised_model.json").toURI());
        serialisedModel = Files.readString(serialisedExample);
    }

    @Test
    public void useModel() throws Exception {
        var credentials = new IdentityCheckCredentialJWTClass();
        credentials.setSub("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce");
        credentials.setAud("https://passport.core.stubs.account.gov.uk");
        credentials.setNbf(1690816091);
        credentials.setIss("https://review-p.build.account.gov.uk");

        var vc = new IdentityCheckCredentialClass();
        vc.setType(List.of(
                VerifiableCredentialType.VERIFIABLE_CREDENTIAL,
                VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL
        ));
        credentials.setVc(vc);

        var evidence = new IdentityCheckClass();
        evidence.setValidityScore(0);
        evidence.setStrengthScore(4);
        evidence.setCi(List.of("D02"));
        evidence.setTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9");
        evidence.setType("IdentityCheck");
        vc.setEvidence(List.of(evidence));

        var credentialSubject = new IdentityCheckSubjectClass();
        vc.setCredentialSubject(credentialSubject);

        var passport = new PassportDetailsClass();
        passport.setExpiryDate("2030-12-12");
        passport.setIcaoIssuerCode("GBR");
        passport.setDocumentNumber("123456789");
        credentialSubject.setPassport(List.of(passport));

        var givenName = new NamePartClass();
        givenName.setType(NamePartClass.NamePartType.GIVEN_NAME);
        givenName.setValue("Kenneth");

        var familyName = new NamePartClass();
        familyName.setType(NamePartClass.NamePartType.FAMILY_NAME);
        familyName.setValue("Decerqueira");

        var name = new NameClass();
        name.setNameParts(List.of(givenName, familyName));
        credentialSubject.setName(List.of(name));

        var birthDate = new BirthDateClass();
        birthDate.setValue("1990-01-23");
        credentialSubject.setBirthDate(List.of(birthDate));

        var json = OBJECT_MAPPER.writeValueAsString(credentials);
        System.out.println(json);

        JSONAssert.assertEquals(serialisedModel, json, false);
    }
}
