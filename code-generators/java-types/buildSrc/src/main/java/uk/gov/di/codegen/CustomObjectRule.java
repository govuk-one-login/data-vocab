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
 * Custom object rule that:
 * - strips the `Class` suffix from generated types (see main README)
 * - adds interfaces for classes with a given suffix
 */
public class CustomObjectRule extends ObjectRule {
    protected CustomObjectRule(RuleFactory ruleFactory, ParcelableHelper parcelableHelper, ReflectionHelper reflectionHelper) {
        super(ruleFactory, parcelableHelper, reflectionHelper);
    }

    @Override
    public JType apply(String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
        var jClass = super.apply(nodeName, node, parent, _package, schema);
        // TODO: Here we can add the annotations

        return jClass;
    }
}
