package uk.gov.di.model.schema;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class VocabSchemaLoader {

    public static final String IDENTITY_CHECK_CREDENTIAL_JWT_NAME = "IdentityCheckCredentialJWT.json";
    public static final String INHERITED_IDENTITY_JWT_NAME = "InheritedIdentityJWT.json";
    public static final String RISK_ASSESSMENT_CREDENTIAL_JWT_NAME = "RiskAssessmentCredentialJWT.json";
    public static final String SECURITY_CHECK_CREDENTIAL_JWT_NAME = "SecurityCheckCredentialJWT.json";

    private VocabSchemaLoader() {
    }

    public static InputStream getSchema(String name) {
        return VocabSchemaLoader.class
                .getResourceAsStream(name);
    }

    public static String getSchemaAsString(String name) {
        try (var stream = VocabSchemaLoader.class.getResourceAsStream(name)){
            if (nonNull(stream)) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
