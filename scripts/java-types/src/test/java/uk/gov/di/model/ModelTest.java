package uk.gov.di.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

class ModelTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final String SERIALISED_MODEL = """
            {
              "aud" : "https://passport.core.stubs.account.gov.uk",
              "iss" : "https://review-p.build.account.gov.uk",
              "nbf" : 1690816091,
              "sub" : "urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce",
              "vc" : {
                "@context" : [ ],
                "credentialSubject" : {
                  "address" : [ ],
                  "birthDate" : [ {
                    "value" : "1990-01-23"
                  } ],
                  "drivingPermit" : [ ],
                  "idCard" : [ ],
                  "name" : [ {
                    "nameParts" : [ {
                      "type" : "GivenName",
                      "value" : "Kenneth"
                    }, {
                      "type" : "FamilyName",
                      "value" : "Decerqueira"
                    } ]
                  } ],
                  "passport" : [ {
                    "documentNumber" : "123456789",
                    "expiryDate" : "2030-12-12",
                    "icaoIssuerCode" : "GBR"
                  } ],
                  "residencePermit" : [ ],
                  "socialSecurityRecord" : [ ]
                },
                "evidence" : [ {
                  "checkDetails" : [ ],
                  "ci" : [ "D02" ],
                  "failedCheckDetails" : [ ],
                  "strengthScore" : 4,
                  "txn" : "5f57a8f2-62b0-4958-9332-06d9f453e5b9",
                  "type" : "IdentityCheck",
                  "validityScore" : 0
                } ],
                "type" : [ "VerifiableCredential", "IdentityCheckCredential" ]
              }
            }
            """.trim();

    @Test
    public void useModel() throws Exception {
        var credentials = new IdentityCheckCredentialJWT()
                .withSub("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce")
                .withAud("https://passport.core.stubs.account.gov.uk")
                .withNbf(1690816091)
                .withIss("https://review-p.build.account.gov.uk");

        var vc = new IdentityCheckCredentialClass().withType(List.of(
                VerifiableCredentialType.VERIFIABLE_CREDENTIAL,
                VerifiableCredentialType.IDENTITY_CHECK_CREDENTIAL
        ));
        credentials.setVc(vc);

        var evidence = new IdentityCheckClass()
                .withValidityScore(0)
                .withStrengthScore(4)
                .withCi(List.of("D02"))
                .withTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9")
                .withType("IdentityCheck");
        vc.setEvidence(List.of(evidence));

        var credentialSubject = new IdentityCheckSubjectClass();
        vc.setCredentialSubject(credentialSubject);

        var passport = new PassportDetailsClass()
                .withExpiryDate("2030-12-12")
                .withIcaoIssuerCode("GBR")
                .withDocumentNumber("123456789");
        credentialSubject.setPassport(List.of(passport));

        var name = new NameClass().withNameParts(List.of(
                new NamePartClass()
                        .withType(NamePartClass.NamePartType.GIVEN_NAME)
                        .withValue("Kenneth"),
                new NamePartClass()
                        .withType(NamePartClass.NamePartType.FAMILY_NAME)
                        .withValue("Decerqueira")
        ));
        credentialSubject.setName(List.of(name));

        var birthDate = new BirthDateClass().withValue("1990-01-23");
        credentialSubject.setBirthDate(List.of(birthDate));

        var json = OBJECT_MAPPER.writeValueAsString(credentials);
        System.out.println(json);

        JSONAssert.assertEquals(SERIALISED_MODEL, json, false);
    }
}
