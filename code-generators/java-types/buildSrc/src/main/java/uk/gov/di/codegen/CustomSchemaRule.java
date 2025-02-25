package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.rules.SchemaRule;

import static uk.gov.di.codegen.CustomUtils.toStream;

/**
 * Custom schema rule that generates all classes contained within the '$defs' section of the json-schema.
 * This may be removed once https://github.com/joelittlejohn/jsonschema2pojo/pull/1523 (or equivalent) is available.
 */
public class CustomSchemaRule extends SchemaRule {
    private static final String DEFINITIONS_PATH = "/$defs";

    private final RuleFactory ruleFactory;

    public CustomSchemaRule(RuleFactory ruleFactory) {
        super(ruleFactory);
        this.ruleFactory = ruleFactory;
    }

    // generatableType is a JPackage
    @Override
    public JType apply(String nodeName, JsonNode schemaNode, JsonNode parent, JClassContainer generatableType, Schema schema) {
        // Process schema as normal
        var type = super.apply(nodeName, schemaNode, parent, generatableType, schema);

        if (isGenericField(nodeName, schemaNode, parent, generatableType, schema) && type instanceof JDefinedClass jDefinedClass && generatableType instanceof JDefinedClass jGeneratableType) {
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

    public boolean isGenericField(String nodeName, JsonNode schemaNode, JsonNode parent, JClassContainer generatableType, Schema schema) {
        return nodeName.equals("credentialSubject"); // TODO: Make me work
    }
}
