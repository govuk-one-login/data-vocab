package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.util.NameHelper;

/**
 * Custom org.jsonschema2pojo.util.NameHelper which removes the <code>Class</code> postfix from class
 * names. For example, the schema defines a class
 * <code>AddressAssertionClass</code> and the resulting class name will be
 * <code>AddressAssertion</code>.
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo/blob/master/jsonschema2pojo-core/src/main/java/org/jsonschema2pojo/util/NameHelper.java">org.jsonschema2pojo.util.NameHelper</a>
 */
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
