package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.JsonPointerUtils;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.PropertyRule;
import org.jsonschema2pojo.rules.RuleFactory;

public class CustomPropertyRule extends PropertyRule {
    private final RuleFactory ruleFactory;

    protected CustomPropertyRule(RuleFactory ruleFactory) {
        super(ruleFactory);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JDefinedClass apply(String nodeName, JsonNode node, JsonNode parent, JDefinedClass jclass, Schema schema) {
        String propertyName = this.ruleFactory.getNameHelper().getPropertyName(nodeName, node);

        JClass jParentClass = jclass._extends();
        if (jParentClass instanceof JDefinedClass jParentDefinedClass) {
            if (jParentDefinedClass.fields().containsKey(propertyName)) {
                var parentType = jParentDefinedClass.fields().get(propertyName).type();
                var propertyType = getPropertyType(nodeName, node, parent, jclass, schema);

                if (isInheritedOrEqual(parentType, propertyType)) {
                    return jclass;
                }
            }
        }

        return super.apply(nodeName, node, parent, jclass, schema);
    }

    private boolean isInheritedOrEqual(JType parentType, JType childType) {
        if (parentType.equals(childType)) {
            return true;
        }
        if (childType instanceof JDefinedClass jDefinedChildClass && parentType instanceof JDefinedClass jDefinedParentClass) {
            return isInheritedOrEqual(parentType, jDefinedChildClass._extends());
        }
        return false;
    }

    private JType getPropertyType(String nodeName, JsonNode node, JsonNode parent, JDefinedClass jclass, Schema schema) {
        String pathToProperty;
        if (schema.getId() != null && schema.getId().getFragment() != null) {
            pathToProperty = "#" + schema.getId().getFragment() + "/properties/" + JsonPointerUtils.encodeReferenceToken(nodeName);
        } else {
            pathToProperty = "#/properties/" + JsonPointerUtils.encodeReferenceToken(nodeName);
        }

        Schema propertySchema = this.ruleFactory.getSchemaStore().create(schema, pathToProperty, this.ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
        return this.ruleFactory.getSchemaRule().apply(nodeName, node, parent, jclass, propertySchema);
    }
}
