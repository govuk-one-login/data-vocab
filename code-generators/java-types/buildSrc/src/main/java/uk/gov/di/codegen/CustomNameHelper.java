package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.util.NameHelper;

public class CustomNameHelper extends NameHelper {
    private static final String CLASS_SUFFIX = "Class";

    public CustomNameHelper(GenerationConfig generationConfig) {
        super(generationConfig);
    }

    @Override
    public String getClassName(String propertyName, JsonNode node) {
        return trimClassName(super.getClassName(propertyName, node));
    }

    public static String trimClassName(String className) {
        return className.endsWith(CLASS_SUFFIX)
                ? className.substring(0, className.length() - CLASS_SUFFIX.length())
                : className;
    }
}
