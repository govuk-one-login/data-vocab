package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.ObjectRule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.util.Map;

import static org.jsonschema2pojo.util.TypeUtil.resolveType;

/**
 * Custom object rule that includes interfaces for classes with a given suffix
 */
public class CustomObjectRule extends ObjectRule {
    private static final Map<String, String> INTERFACE_MAPPING = Map.of(
            "CredentialJWTClass", "uk.gov.di.model.CredentialJWTClass",
            "CredentialClass", "uk.gov.di.model.CredentialClass"
    );

    protected CustomObjectRule(RuleFactory ruleFactory, ParcelableHelper parcelableHelper, ReflectionHelper reflectionHelper) {
        super(ruleFactory, parcelableHelper, reflectionHelper);
    }

    @Override
    public JType apply(String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
        var jClass = super.apply(nodeName, node, parent, _package, schema);

        for (var suffix : INTERFACE_MAPPING.keySet()) {
            if (nodeName.endsWith(suffix) && jClass instanceof JDefinedClass jDefinedClass) {
                jDefinedClass._implements(resolveType(jDefinedClass._package(), INTERFACE_MAPPING.get(suffix)));
            }
        }

        return jClass;
    }
}
