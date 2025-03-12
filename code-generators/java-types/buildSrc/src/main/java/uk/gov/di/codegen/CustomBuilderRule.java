package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.util.Objects;

/**
 * Custom implementation of the {@link org.jsonschema2pojo.rules.BuilderRule}
 * which generates a more simplified Builder without using generics.
 * <p />
 * Ideally this rule would have extended from the {@link org.jsonschema2pojo.rules.BuilderRule}
 * however, unlike most of the other rules implemented within the jsonschema2pojo
 * library the <code>BuilderRule</code> constructor is package protected. The
 * code except the {@link uk.gov.di.codegen.CustomBuilderRule#apply(String, JsonNode, JsonNode, JDefinedClass, Schema)}
 * method is identical to the original {@link org.jsonschema2pojo.rules.BuilderRule}
 * implementation.
 * <p />
 *
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo/blob/master/jsonschema2pojo-core/src/main/java/org/jsonschema2pojo/rules/BuilderRule.java">org.jsonschema2pojo.rules.BuilderRule</a>
 */
public class CustomBuilderRule implements Rule<JDefinedClass, JDefinedClass> {
    private final RuleFactory ruleFactory;
    private final ReflectionHelper reflectionHelper;
    private static final String OBJECT_CLASS_NAME = "java.lang.Object";

    public CustomBuilderRule(RuleFactory ruleFactory, ReflectionHelper reflectionHelper) {
        this.ruleFactory = ruleFactory;
        this.reflectionHelper = reflectionHelper;
    }

    public JDefinedClass apply(String nodeName, JsonNode node, JsonNode parent, JDefinedClass instanceClass, Schema currentSchema) {
        JDefinedClass concreteBuilderClass;
        JDefinedClass builderClass;
        try {
            String concreteBuilderClassName = this.ruleFactory.getNameHelper().getBuilderClassName(instanceClass);
            String builderClassName = this.ruleFactory.getNameHelper().getBaseBuilderClassName(instanceClass);
            builderClass = instanceClass._class(JMod.PUBLIC | JMod.ABSTRACT | JMod.STATIC, builderClassName);
            concreteBuilderClass = instanceClass._class(JMod.PUBLIC | JMod.STATIC, concreteBuilderClassName);
            concreteBuilderClass._extends(builderClass);
        } catch (JClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        JClass parentBuilderClass = null;
        JClass parentClass = instanceClass._extends();
        if (!parentClass.isPrimitive() && !this.reflectionHelper.isFinal(parentClass) && !Objects.equals(parentClass.fullName(), OBJECT_CLASS_NAME)) {
            parentBuilderClass = this.reflectionHelper.getBaseBuilderClass(parentClass);
        }

        JMethod buildMethod = builderClass.method(JMod.PUBLIC, instanceClass, "build");
        JBlock body = buildMethod.body();

        if (parentBuilderClass == null) {
            JFieldVar instanceField = builderClass.field(JMod.PROTECTED, instanceClass, "instance");
            JVar result = body.decl(instanceClass, "result");
            body.assign(result, JExpr._this().ref(instanceField));
            body.assign(JExpr._this().ref(instanceField), JExpr._null());
            body._return(result);
            this.generateNoArgsBuilderConstructors(instanceClass, builderClass, concreteBuilderClass);
        } else {
            builderClass._extends(parentBuilderClass);
            buildMethod.annotate(Override.class);
            body._return(JExpr.cast(instanceClass, JExpr._super().invoke("build")));
            this.generateNoArgsBuilderConstructors(instanceClass, builderClass, concreteBuilderClass);
        }

        JMethod builderMethod = instanceClass.method(JMod.PUBLIC | JMod.STATIC, builderClass, "builder");
        JBlock builderBody = builderMethod.body();
        builderBody._return(JExpr._new(concreteBuilderClass));
        return builderClass;
    }

    private void generateNoArgsBuilderConstructors(JDefinedClass instanceClass, JDefinedClass baseBuilderClass, JDefinedClass builderClass) {
        this.generateNoArgsBaseBuilderConstructor(instanceClass, baseBuilderClass, builderClass);
        this.generateNoArgsBuilderConstructor(baseBuilderClass, builderClass);
    }

    private void generateNoArgsBuilderConstructor(JDefinedClass baseBuilderClass, JDefinedClass builderClass) {
        JMethod noArgsConstructor = builderClass.constructor(JMod.PUBLIC);
        JBlock constructorBlock = noArgsConstructor.body();
        if (!baseBuilderClass.isPrimitive() && !this.reflectionHelper.isFinal(baseBuilderClass) && !Objects.equals(baseBuilderClass.fullName(), OBJECT_CLASS_NAME)) {
            constructorBlock.invoke("super");
        }
    }

    private void generateNoArgsBaseBuilderConstructor(JDefinedClass instanceClass, JDefinedClass builderClass, JDefinedClass concreteBuilderClass) {
        JMethod noArgsConstructor = builderClass.constructor(JMod.PUBLIC);
        JAnnotationUse warningSuppression = noArgsConstructor.annotate(SuppressWarnings.class);
        warningSuppression.param("value", "unchecked");
        JBlock constructorBlock = noArgsConstructor.body();
        JFieldVar instanceField = this.reflectionHelper.searchClassAndSuperClassesForField("instance", builderClass);
        JClass parentClass = builderClass._extends();
        if (!parentClass.isPrimitive() && !this.reflectionHelper.isFinal(parentClass) && !Objects.equals(parentClass.fullName(), OBJECT_CLASS_NAME)) {
            constructorBlock.invoke("super");
        }

        constructorBlock.directStatement("// Skip initialization when called from subclass");
        JInvocation comparison = JExpr._this().invoke("getClass").invoke("equals").arg(JExpr.dotclass(concreteBuilderClass));
        JConditional ifNotSubclass = constructorBlock._if(comparison);
        ifNotSubclass._then().assign(JExpr._this().ref(instanceField), JExpr.cast(instanceField.type(), JExpr._new(instanceClass)));
    }
}
