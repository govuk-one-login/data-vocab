package uk.gov.di.model.schema;

import java.io.InputStream;

public class VocabSchemaLoader {

    private VocabSchemaLoader() {
    }

    public static InputStream getSchema(String name) {
        return VocabSchemaLoader.class
                .getResourceAsStream(name);
    }
}
