package uk.gov.di.model.schema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.di.model.schema.VocabSchemaLoader.IDENTITY_CHECK_CREDENTIAL_JWT_NAME;
import static uk.gov.di.model.schema.VocabSchemaLoader.INHERITED_IDENTITY_JWT_NAME;
import static uk.gov.di.model.schema.VocabSchemaLoader.RISK_ASSESSMENT_CREDENTIAL_JWT_NAME;
import static uk.gov.di.model.schema.VocabSchemaLoader.SECURITY_CHECK_CREDENTIAL_JWT_NAME;

class VocabSchemaLoaderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            IDENTITY_CHECK_CREDENTIAL_JWT_NAME,
            INHERITED_IDENTITY_JWT_NAME,
            RISK_ASSESSMENT_CREDENTIAL_JWT_NAME,
            SECURITY_CHECK_CREDENTIAL_JWT_NAME,
    })
    void shouldReturnInputStreamWhenPassedValidSchemaNames(String schemaName) throws IOException {
        try (var schema = VocabSchemaLoader.getSchema(schemaName)) {
            assertNotNull(schema);
        }
    }

    @Test
    void shouldReturnNullStreamWhenPassedInvalidSchemaName() {
        assertNull(VocabSchemaLoader.getSchema("does-not-exist.json"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            IDENTITY_CHECK_CREDENTIAL_JWT_NAME,
            INHERITED_IDENTITY_JWT_NAME,
            RISK_ASSESSMENT_CREDENTIAL_JWT_NAME,
            SECURITY_CHECK_CREDENTIAL_JWT_NAME,
    })
    void shouldReturnSchemaAsStringWhenPassedValidSchemaNames(String schemaName) {
        var schema = VocabSchemaLoader.getSchemaAsString(schemaName);

        assertNotNull(schema);
        assertTrue(schema.trim().startsWith("{"));
        assertTrue(schema.trim().endsWith("}"));
    }

    @Test
    void shouldReturnNullStringWhenPassedInvalidSchemaName() {
        assertNull(VocabSchemaLoader.getSchemaAsString("does-not-exist.json"));
    }
}
