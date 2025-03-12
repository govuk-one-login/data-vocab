package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.PropertyRule;
import org.jsonschema2pojo.rules.RuleFactory;

/**
 * The JSON Schema contains definitions for all classes and there definitions.
 * However, many of the class definitions contain fields which are also defined
 * in their super classes. This results in POJO being defined in both
 * superclasses and subclasses, if these are defined as being identical or
 * where the type of the property extends from the type of the superclass
 * property.
 * <p />
 * This {@link org.jsonschema2pojo.rules.PropertyRule} looks at each classes
 * super class to see if the parent contains a field of the same name. If the
 * property is found it will just return the defined class as is, otherwise it
 * uses the default behaviour of
 * {@link org.jsonschema2pojo.rules.PropertyRule#apply(String, JsonNode, JsonNode, JDefinedClass, Schema)}
 * to create the field, setters, getters and builder methods.
 */
public class CustomPropertyRule extends PropertyRule {
    private final RuleFactory ruleFactory;

    protected CustomPropertyRule(RuleFactory ruleFactory) {
        super(ruleFactory);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JDefinedClass apply(String nodeName, JsonNode node, JsonNode parent, JDefinedClass jclass, Schema schema) {
        String propertyName = this.ruleFactory.getNameHelper().getPropertyName(nodeName, node);

        if (isDefinedInParent(propertyName, jclass)) {
            return jclass;
        }

        return super.apply(nodeName, node, parent, jclass, schema);
    }

    /**
     * Iterates through the class hierarchy to determine whether the field is
     * defined in a parent class.
     *
     * @param fieldName The name of the field to look for
     * @param jclass The class to look at the parent field
     * @return true if the field is found
     */
    private boolean isDefinedInParent(String fieldName, JDefinedClass jclass) {
        JClass jParentClass = jclass._extends();
        if (jParentClass instanceof JDefinedClass jDefinedParentClass) {
            JFieldVar jFieldVar = jDefinedParentClass.fields().get(fieldName);
            if (jFieldVar != null) {
                return true;
            }
            return isDefinedInParent(fieldName, jDefinedParentClass);
        }
        return false;
    }
}
