package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.ObjectRule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import static uk.gov.di.codegen.CustomUtils.toStream;

/**
 * Custom object rule that:
 * - strips the `Class` suffix from generated types (see main README)
 * - adds interfaces for classes with a given suffix
 */
public class CustomObjectRule extends ObjectRule {
    private final RuleFactory ruleFactory;

    protected CustomObjectRule(RuleFactory ruleFactory, ParcelableHelper parcelableHelper, ReflectionHelper reflectionHelper) {
        super(ruleFactory, parcelableHelper, reflectionHelper);
        this.ruleFactory = ruleFactory;
    }

    @Override
    public JType apply(String nodeName, JsonNode node, JsonNode parent, JPackage _package, Schema schema) {
        JType jClass = super.apply(nodeName, node, parent, _package, schema);
        if (jClass instanceof JDefinedClass jDefinedClass) {
            addDefaultGenericOverrides(jDefinedClass, node, parent, schema);
            makePrivateMembersProtected(jDefinedClass);
            replaceBuilderJTypeVarWithConcreteType(jDefinedClass);
            duplicateBaseBuilderMethods(jDefinedClass);
        }

        return jClass;
    }

    private void addDefaultGenericOverrides(JDefinedClass jClass, JsonNode node, JsonNode parent, Schema schema) {
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

    private void makePrivateMembersProtected(JDefinedClass jClass) {
        for (var jField : jClass.fields().values()) {
            if (jField.mods().getValue() == 4) {
                jField.mods().setProtected();
            }
        }
    }

    private void duplicateBaseBuilderMethods(JDefinedClass jClass) {
        JDefinedClass builderClass = this.ruleFactory.getReflectionHelper().getBaseBuilderClass(jClass);
        if (builderClass == null) {
            return;
        }

        Collection<JClass> jClassTypeParameters = jClass._extends().getTypeParameters();
        for (JClass parent : getJClassParentIterable(builderClass)) {
            if (!(parent.erasure() instanceof JDefinedClass jParent)) {
                continue;
            }
            jParent.methods().stream()
                    .filter(method -> method.type().equals(jParent))
                    .forEach(method -> {
                JVar paramType = method.params().get(0);

                if (builderClass.getMethod(method.name(), new JType[]{ paramType.type() }) != null) {
                    return;
                }

                for(JClass currentParam : jClassTypeParameters) {
                    if (paramType.type() instanceof JClass jClassParam && isDerivedFrom(currentParam, jClassParam)) {
                        paramType = jClass.field(paramType.mods().getValue(), currentParam, paramType.name());
                        jClass.removeField((JFieldVar)paramType);
                    }
                }

                addInnerBuilderMethod(builderClass, jClass, paramType, method.name());
            });
        }
    }

    private void addInnerBuilderMethod(JDefinedClass builderClass, JDefinedClass c, JVar field, String propertyName) {
        JMethod builderMethod = builderClass.method(1, builderClass, propertyName);
        JVar param = builderMethod.param(field.type(), field.name());
        JBlock body = builderMethod.body();
        body.assign(JExpr.ref(JExpr.cast(c, JExpr._this().ref("instance")), field), param);
        body._return(JExpr._this());
    }

    private void replaceBuilderJTypeVarWithConcreteType(JDefinedClass jDefinedClass) {
        JDefinedClass builderClass = this.ruleFactory.getReflectionHelper().getBaseBuilderClass(jDefinedClass);
        if (builderClass == null) {
            return;
        }

        for(JMethod method : builderClass.methods()) {
            for(JVar param : method.params()) {
                if (param.type() instanceof JTypeVar jTypeVar) {
                    param.type(jTypeVar._extends());
                }
            }
        }
    }

    private static boolean isDerivedFrom(JClass jClass, JClass jParent) {
        for (JClass currentParent : getJClassParentIterable(jClass)) {
            if (currentParent.equals(jParent)) {
                return true;
            }
        }
        return false;
    }

    private static Iterable<JClass> getJClassParentIterable(JClass object) {
        return () -> new Iterator<>() {
            JClass currentObject = object;

            @Override
            public boolean hasNext() {
                return !currentObject.name().equals("Object");
            }

            @Override
            public JClass next() {
                currentObject = currentObject._extends();
                return currentObject;
            }
        };
    }
}
