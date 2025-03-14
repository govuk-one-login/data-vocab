package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.rules.SchemaRule;

import java.util.Objects;

import static uk.gov.di.codegen.CustomUtils.toStream;

/**
 * Custom schema rule that generates all classes contained within the '$defs' section of the json-schema.
 * This may be removed once https://github.com/joelittlejohn/jsonschema2pojo/pull/1523 (or equivalent) is available.
 *
 * Assuming that a type such as <code>AddressCredential</code> extends from
 * the type <code>VerifiableCredential</code> each of these classes define
 * the field <code>credentialSubject</code>. By default, jsonschema2pojo
 * will create the field <code>credentialSubject</code> in both
 * <code>AddressCredential</code> and <code>VerifiableCredential</code> of
 * the types <code>AddressAssertion</code> and
 * <code>CredentialSubject</code> respectively. The
 * <code>AddressAssertion</code> class extends from
 * <code>CredentialSubject</code>. In practice this means that serialisers
 * such as GSON will fail to serialise or deserialise the POJO due to
 * duplicate fields being detected.
 * <p />
 * To resolve this issue the <code>CustomSchemaRule</code> inspects the class
 * hierarchy and when it finds a duplicate field of a different but inherited
 * type it will make the property into a generic type.
 */
public class CustomSchemaRule extends SchemaRule {
    private static final String DEFINITIONS_PATH = "/$defs";

    private final RuleFactory ruleFactory;

    public CustomSchemaRule(RuleFactory ruleFactory) {
        super(ruleFactory);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JType apply(String nodeName, JsonNode schemaNode, JsonNode parent, JClassContainer generatableType, Schema schema) {
        // Process schema as normal
        var type = super.apply(nodeName, schemaNode, parent, generatableType, schema);

        if (type instanceof JDefinedClass jDefinedClass && generatableType instanceof JDefinedClass jGeneratableType && isGenericField(nodeName, schema)) {
            type = jGeneratableType.generify(jDefinedClass.name() + "T", jDefinedClass);
        }

        // Process definitions
        toStream(schemaNode.at(DEFINITIONS_PATH).fields()).forEach(definition -> {
            final var definitionSchema = ruleFactory.getSchemaStore().create(
                    schema,
                    "#" + DEFINITIONS_PATH + "/" + definition.getKey(),
                    ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
            if (!definitionSchema.isGenerated()) {
                apply(definition.getKey(), definition.getValue(), schemaNode, generatableType, definitionSchema);
            }
        });

        return type;
    }

    private static boolean isGenericField(String nodeName, Schema schema) {
        String propertyFragment = getClassFragmentFromPropertyFragment(schema);
        JsonNode definitions = schema.getGrandParent().getContent().get("$defs");

        return toStream(definitions.fields()).anyMatch(entry -> {
            JsonNode classNode = entry.getValue();
            JsonNode extendsNode = classNode.get("extends");
            if (extendsNode == null) {
                return false;
            }

            JsonNode reference = extendsNode.get("$ref");
            if (reference == null || !reference.asText().equals("#" + propertyFragment)) {
                return false;
            }

            JsonNode nodeProperty = classNode.get("properties").get(nodeName);
            if (nodeProperty == null) {
                return false;
            }

            JsonNode content = schema.getContent();
            return !Objects.equals(content.get("$ref"), nodeProperty.get("$ref"));
        });
    }

    private static String getClassFragmentFromPropertyFragment(Schema schema) {
        String propertyFragment = schema.getId().getFragment();
        int propertiesIndex = propertyFragment.indexOf("/properties");
        if (propertiesIndex > 0) {
            propertyFragment = propertyFragment.substring(0, propertiesIndex);
        }
        return propertyFragment;
    }
}
