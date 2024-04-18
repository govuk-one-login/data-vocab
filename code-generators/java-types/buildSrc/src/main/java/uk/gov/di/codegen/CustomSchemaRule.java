package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.rules.SchemaRule;

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

    @Override
    public JType apply(String nodeName, JsonNode schemaNode, JsonNode parent, JClassContainer generatableType, Schema schema) {
        // Process schema as normal
        var type = super.apply(nodeName, schemaNode, parent, generatableType, schema);

        // Get definitions
        final var definitions = schemaNode.at(DEFINITIONS_PATH).fields();

        // Process definitions
        while (definitions.hasNext()) {
            final var definition = definitions.next();

            final var definitionSchema = ruleFactory.getSchemaStore().create(
                    schema,
                    "#" + DEFINITIONS_PATH + "/" + definition.getKey(),
                    ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
            if (definitionSchema.isGenerated()) {
                continue;
            }

            apply(definition.getKey(), definition.getValue(), schemaNode, generatableType, definitionSchema);
        }

        return type;
    }
}
