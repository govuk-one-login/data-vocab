package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
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

        FieldTuple jFieldVar = getParentField(propertyName, jclass);
        if (jFieldVar != null) {
            return jclass;
        }

        return super.apply(nodeName, node, parent, jclass, schema);
    }

    private FieldTuple getParentField(String fieldName, JDefinedClass jclass) {
        JClass jParentClass = jclass._extends();
        if (jParentClass instanceof JDefinedClass jDefinedParentClass) {
            JFieldVar jFieldVar = jDefinedParentClass.fields().get(fieldName);
            if (jFieldVar != null) {
                return new FieldTuple(jDefinedParentClass, jFieldVar);
            }
            return getParentField(fieldName, jDefinedParentClass);
        }
        return null;
    }

    private record FieldTuple(JDefinedClass owner, JFieldVar fieldVar) {}
}
