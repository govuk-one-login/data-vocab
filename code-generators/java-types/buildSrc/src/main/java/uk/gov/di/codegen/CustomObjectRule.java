package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.apache.commons.lang.StringUtils;
import org.jsonschema2pojo.JsonPointerUtils;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.ObjectRule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.di.codegen.CustomUtils.toStream;

/**
 * Custom object rule that:
 * - strips the `Class` suffix from generated types (see main README)
 * - adds interfaces for classes with a given suffix
 */
public class CustomObjectRule extends ObjectRule {
    private RuleFactory ruleFactory;
    protected CustomObjectRule(RuleFactory ruleFactory, ParcelableHelper parcelableHelper, ReflectionHelper reflectionHelper) {
        super(ruleFactory, parcelableHelper, reflectionHelper);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JType apply(String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
        JType jClass = super.apply(nodeName, node, parent, _package, schema);
        if (jClass instanceof JDefinedClass jDefinedClass) {
            addDefaultGenericOverrides(jDefinedClass, nodeName, node, parent, _package, schema);
        }

        return jClass;
    }

    private void addDefaultGenericOverrides(JDefinedClass jClass, String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
        JClass superType = jClass._extends();
        if (!(superType instanceof JDefinedClass superClass)) {
            return;
        }

        HashMap<String, JType> foundOverrides = new HashMap<>();

        toStream(node.get("properties").fieldNames()).forEach(fieldName -> {
            var field = superClass.fields().get(fieldName);
            if (field == null) {
                return;
            }

            String pathToProperty = "#" + schema.getId().getFragment() + "/properties/" + fieldName;

            Schema propertySchema = this.ruleFactory.getSchemaStore().create(schema.getParent().getParent(), pathToProperty, this.ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
            JType propertyType = this.ruleFactory.getSchemaRule().apply(fieldName, propertySchema.getContent(), parent, jClass, propertySchema);

            foundOverrides.put(field.type().name(), propertyType);
        });

        JDefinedClass narrowedObject = jClass;
        for(var typeParam : superType.typeParams()) {
            JType override = foundOverrides.get(typeParam.name());
            if (override == null) {
                override = typeParam._extends();
            }

            narrowedObject = narrowedObject._extends(superType.narrow(override));
        }
    }
}
