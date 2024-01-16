package uk.gov.di.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var credentials = new IdentityCheckCredentialJWT();
        credentials.setSub("urn:fdc:gov.uk:2022:954bc117-731b-41cd-86cf-dfb4e7940fce");
        credentials.setAud("https://passport.core.stubs.account.gov.uk");
        credentials.setNbf(1690816091);
        credentials.setIss("https://review-p.build.account.gov.uk");

        var vc = new IdentityCheckCredentialClass();

        var evidence = new IdentityCheckClass__1();
        evidence.setValidityScore(0);
        evidence.setStrengthScore(4);
        evidence.setCi(List.of("D02"));
        vc.setEvidence(List.of(
                evidence
        ));
        credentials.setVc(vc);
        evidence.setTxn("5f57a8f2-62b0-4958-9332-06d9f453e5b9");
        evidence.setType("IdentityCheck");

        var credentialSubject = new IdentityCheckSubjectClass__1();

        var passport = new PassportDetailsClass__1();
        passport.setExpiryDate("2030-12-12");
        passport.setIcaoIssuerCode("GBR");
        passport.setDocumentNumber("123456789");
        credentialSubject.setPassport(List.of(passport));

        var name = new NameClass__4();
        var namePart = new NamePartClass__4();
        namePart.setType(NamePartClass__4.NamePartType.GIVEN_NAME);
        namePart.setValue("Kenneth");
        namePart.setType(NamePartClass__4.NamePartType.FAMILY_NAME);
        namePart.setValue("Decerqueira");
        name.setNameParts(List.of(namePart));
        credentialSubject.setName(List.of(name));

        var birthDate = new BirthDateClass__2();
        birthDate.setValue("1990-01-23");
        credentialSubject.setBirthDate(List.of(birthDate));
        vc.setCredentialSubject(credentialSubject);
        vc.setType(
                List.of(
                        VerifiableCredentialType__.VERIFIABLE_CREDENTIAL,
                        VerifiableCredentialType__.IDENTITY_CHECK_CREDENTIAL
                )
        );

        var json = OBJECT_MAPPER.writeValueAsString(credentials);
        System.out.println(json);
        assertEquals(SERIALISED_MODEL, json);
    }
}
