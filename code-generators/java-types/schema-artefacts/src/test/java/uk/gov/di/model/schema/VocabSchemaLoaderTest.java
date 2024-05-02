package uk.gov.di.model.schema;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class VocabSchemaLoaderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "IdentityCheckCredentialJWT.json",
            "InheritedIdentityJWT.json",
            "RiskAssessmentCredentialJWT.json",
            "SecurityCheckCredentialJWT.json",
    })
    void getSchema(String schemaName) throws IOException {
        try (var schema = VocabSchemaLoader.getSchema(schemaName)) {
            assertNotNull(schema);
        }
    }
}
