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
import com.sun.codemodel.JVar;
import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ReflectionHelper;

import java.util.Objects;

public class CustomBuilderRule implements Rule<JDefinedClass, JDefinedClass> {
    private final RuleFactory ruleFactory;
    private final ReflectionHelper reflectionHelper;

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
            builderClass = instanceClass._class(49, builderClassName);
            concreteBuilderClass = instanceClass._class(17, concreteBuilderClassName);
            concreteBuilderClass._extends(builderClass);
        } catch (JClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        JClass parentBuilderClass = null;
        JClass parentClass = instanceClass._extends();
        if (!parentClass.isPrimitive() && !this.reflectionHelper.isFinal(parentClass) && !Objects.equals(parentClass.fullName(), "java.lang.Object")) {
            parentBuilderClass = this.reflectionHelper.getBaseBuilderClass(parentClass);
        }

        JMethod buildMethod = builderClass.method(1, instanceClass, "build");
        JBlock body = buildMethod.body();

        if (parentBuilderClass == null) {
            JFieldVar instanceField = builderClass.field(2, instanceClass, "instance");
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

        JMethod builderMethod = instanceClass.method(17, builderClass, "builder");
        JBlock builderBody = builderMethod.body();
        builderBody._return(JExpr._new(concreteBuilderClass));
        return builderClass;
    }

    private void generateNoArgsBuilderConstructors(JDefinedClass instanceClass, JDefinedClass baseBuilderClass, JDefinedClass builderClass) {
        this.generateNoArgsBaseBuilderConstructor(instanceClass, baseBuilderClass, builderClass);
        this.generateNoArgsBuilderConstructor(instanceClass, baseBuilderClass, builderClass);
    }

    private void generateNoArgsBuilderConstructor(JDefinedClass instanceClass, JDefinedClass baseBuilderClass, JDefinedClass builderClass) {
        JMethod noArgsConstructor = builderClass.constructor(1);
        JBlock constructorBlock = noArgsConstructor.body();
        if (!baseBuilderClass.isPrimitive() && !this.reflectionHelper.isFinal(baseBuilderClass) && !Objects.equals(baseBuilderClass.fullName(), "java.lang.Object")) {
            constructorBlock.invoke("super");
        }

    }

    private void generateNoArgsBaseBuilderConstructor(JDefinedClass instanceClass, JDefinedClass builderClass, JDefinedClass concreteBuilderClass) {
        JMethod noArgsConstructor = builderClass.constructor(1);
        JAnnotationUse warningSuppression = noArgsConstructor.annotate(SuppressWarnings.class);
        warningSuppression.param("value", "unchecked");
        JBlock constructorBlock = noArgsConstructor.body();
        JFieldVar instanceField = this.reflectionHelper.searchClassAndSuperClassesForField("instance", builderClass);
        JClass parentClass = builderClass._extends();
        if (!parentClass.isPrimitive() && !this.reflectionHelper.isFinal(parentClass) && !Objects.equals(parentClass.fullName(), "java.lang.Object")) {
            constructorBlock.invoke("super");
        }

        constructorBlock.directStatement("// Skip initialization when called from subclass");
        JInvocation comparison = JExpr._this().invoke("getClass").invoke("equals").arg(JExpr.dotclass(concreteBuilderClass));
        JConditional ifNotSubclass = constructorBlock._if(comparison);
        ifNotSubclass._then().assign(JExpr._this().ref(instanceField), JExpr.cast(instanceField.type(), JExpr._new(instanceClass)));
    }
}
