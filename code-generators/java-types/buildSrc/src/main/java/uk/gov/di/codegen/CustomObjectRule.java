package uk.gov.di.codegen;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
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
 * A custom {@link org.jsonschema2pojo.rules.ObjectRule} which mutates POJOs and
 * their builders to ensure that it's the correct shape we're expecting.
 */
public class CustomObjectRule extends ObjectRule {
    /**
     * Reference to the {@link org.jsonschema2pojo.rules.RuleFactory} which created this rule.
     * The {@link org.jsonschema2pojo.rules.ObjectRule} class defines <code>ruleFactory</code>
     * as private and does not provide a getter so we define it again here.
     */
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

    /**
     * Adds generic overrides to POJOs which extend from generic POJOs.
     * <p />
     * Assuming that a type such as <code>AddressCredential</code> extends from
     * the type <code>VerifiableCredential</code> each of these classes define
     * the field <code>credentialSubject</code>. By default, jsonschema2pojo
     * will create the field <code>credentialSubject</code> in both
     * <code>AddressCredential</code> and <code>VerifiableCredential</code> of
     * the types <code>AddressAssertion</code> and
     * <code>CredentialSubject</code> respectively. The
     * <code>AddressAssertion</code> class extends from
     * <code>CredentialSubject</code>. In practice this means that serialisers
     * such as GSON will fail to serialise or deserialise the POJO due to
     * duplicate fields being detected.
     * <p />
     * To resolve this issue {@link uk.gov.di.codegen.CustomSchemaRule} will
     * inspect the class hierarchy and when it finds a duplicate field of a
     * different but inherited type it will make the property into a generic
     * type.
     * <p />
     * However, when the Builder is generated it is created this the name
     * of the generic type. For example the following will be generated where
     * the <code>AddressCredential</code> extends
     * <code>VerifiableCredential</code> but it does not set the correct type
     * for the <code>CredentialSubjectT</code> so it will default to
     * <code>CredentialSubject</code>.
     *
     * <pre>{@code
     *   public class VerifiableCredential<CredentialSubjectT extends CredentialSubject> {...}
     *
     *   public class AddressCredential extends VerifiableCredential {...}
     * }
     * </pre>
     *
     * This method adds the generic override using the correct type so that the
     * following code is generated:
     *
     * <pre>{@code
     * public class AddressCredential extends VerifiableCredential<AddressAssertion> {}
     * }</pre>
     *
     * @param jClass The defined class
     * @param node The JSON Schema node
     * @param parent The parent JSON Schema node
     * @param schema The schema
     */
    private void addDefaultGenericOverrides(JDefinedClass jClass, JsonNode node, JsonNode parent, Schema schema) {
        JClass superType = jClass._extends();
        if (!(superType instanceof JDefinedClass superClass)) {
            return;
        }

        HashMap<String, JType> foundOverrides = new HashMap<>();

        var properties = node.get("properties");
        if (properties != null) {
            toStream(properties.fieldNames()).forEach(fieldName -> {
                var field = superClass.fields().get(fieldName);
                if (field == null) {
                    return;
                }

                String pathToProperty = "#" + schema.getId().getFragment() + "/properties/" + fieldName;

                Schema propertySchema = this.ruleFactory.getSchemaStore().create(schema.getParent().getParent(), pathToProperty, this.ruleFactory.getGenerationConfig().getRefFragmentPathDelimiters());
                JType propertyType = this.ruleFactory.getSchemaRule().apply(fieldName, propertySchema.getContent(), parent, jClass, propertySchema);

                foundOverrides.put(field.type().name(), propertyType);
            });
        }

        JDefinedClass narrowedObject = jClass;
        for(var typeParam : superType.typeParams()) {
            JType override = foundOverrides.get(typeParam.name());
            if (override == null) {
                override = typeParam._extends();
            }

            narrowedObject = narrowedObject._extends(superType.narrow(override));
        }
    }

    /**
     * Makes all <code>private</code> members <code>protected</code> in the
     * POJO class.
     * <p />
     * By default, the {@link org.jsonschema2pojo.rules.PropertyRule}
     * will add members to the POJO class as <code>private</code>, as well as
     * getters and setters. The inner class ...BuilderBase will sets these
     * fields directly. However, when a subclass extends from the BaseBuilder
     * the Builder needs to directly write to the fields of the parent class.
     * Rather than using complex code to use the setters instead the fields are
     * marked as <code>protected</code> so that they can be easily accessed from
     * subclasses.
     *
     * @param jClass The class defined which requires its <code>private</code>
     *               members made <code>protected</code>.
     */
    private void makePrivateMembersProtected(JDefinedClass jClass) {
        for (var jField : jClass.fields().values()) {
            if (jField.mods().getValue() == JMod.PRIVATE) {
                jField.mods().setProtected();
            }
        }
    }

    /**
     * By default if a POJO extends from another POJO and builders are being
     * created, then the Builder for the subclass will extend from the Builder
     * of the parent POJO. However, a problem occurs when you call a method from
     * the subclass POJO as it will return the wrong type.
     * <p />
     * As an illustration the <code>IdentityCheckCredential</code> extends from
     * <code>IdentityCheckCredential</code>, and in turn the
     * <code>IdentityCheckCredentialBuilderBase</code> extends from the
     * <code>VerifiableCredentialBuilderBase</code>. As shown below.
     * <pre>{@code
     * public static abstract class VerifiableCredentialBuilderBase {
     *   ...
     *   public VerifiableCredential build() {
     *     ...
     *   }
     *
     *   public VerifiableCredentialBuilderBase withCredentialSubject(CredentialSubjectT credentialSubject) {
     *     ...
     *   }
     * }}
     * </pre>
     *
     * <pre>{@code
     * public static abstract class IdentityCheckCredentialBuilderBase
     *     extends VerifiableCredentialBuilderBase {
     *   ...
     *   @Override
     *   public IdentityCheckCredential build() {
     *     ...
     *   }
     *
     *   public IdentityCheckCredentialBuilderBase withEvidence(List<IdentityCheck> evidence) {
     *     ...
     *   }
     *
     *   public IdentityCheckCredentialBuilderBase withType(List<VerifiableCredentialType> type) {
     *     ...
     *   }
     * }}</pre>
     *
     * Assuming we use the <code>IdentityCheckCredentialBuilder</code> as follows
     * it will fail to compile. This is due to the <code>withCredentialSubject</code>
     * being implemented in the <code>VerifiableCredentialBuilderBase</code> and
     * returning and instance of <code>VerifiableCredentialBuilderBase</code>
     * rather than <code>IdentityCheckCredentialBuilderBase</code> which is what
     * we would expect.
     *
     * <pre>{@code
     * IdentityCheckCredential identityCheck = new IdentityCheckCredentialBuilder()
     *   .withType(List.of(VERIFIABLE_CREDENTIAL, IDENTITY_CHECK_CREDENTIAL))
     *   .withCredentialSubject(...)
     *   .withEvidenceList(...)
     *   .build();
     * }</pre>
     *
     * In order to get around this we duplicate each of the methods within the
     * subclass returning the type of the Builder we're expecting which allows
     * our example to work.
     *
     * <pre>{@code
     * public static abstract class IdentityCheckCredentialBuilderBase
     *     extends VerifiableCredentialBuilderBase
     * {
     *   ...
     *
     *   @Override
     *   public IdentityCheckCredential build() {
     *     ...
     *   }
     *
     *   public IdentityCheckCredential.IdentityCheckCredentialBuilderBase withCredentialSubject(IdentityCheckSubject credentialSubject) {
     *     ...
     *   }
     *
     *   public IdentityCheckCredential.IdentityCheckCredentialBuilderBase withEvidence(List<IdentityCheck> evidence) {
     *     ...
     *   }
     *
     *   public IdentityCheckCredential.IdentityCheckCredentialBuilderBase withType(List<VerifiableCredentialType> type) {
     *     ...
     *   }
     * }
     * }</pre>
     *
     * @param jClass The define POJO class. Assuming a Builder has already been
     *               generated for the POJO class the existing methods in the
     *               superclass will be copied to this class and returning the
     *               correct Builder class type.
     */
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

    /**
     * For the given defined builder class generates builder method. For
     * example, let us assuming that:
     * <ul>
     *     <li>The <code>builderClass</code> is the class definition of
     *     <code>AddressCredentialBuilderBase</code></li>
     *     <li>The <code>c</code> is the class definition of the POJO
     *     <code>AddressCredential</code></li>
     *     <li>The <code>field</code> is the definition of the
     *     <code>context</code> field</li>
     *     <li>The <code>propertyName</code> is <code>withContext</code></li>
     * </ul>
     *
     * This method would then generate the method <code>withContext</code> in
     * the <code>AddressCredentialBuilderBase</code> with the following definition:
     * <pre>{@code
     * public AddressCredential.AddressCredentialBuilderBase withContext(List<String> context) {
     *   ((AddressCredential) this.instance).context = context;
     *   return this;
     * }
     * }</pre>
     *
     * @param builderClass The definition of the abstract builder class
     * @param c The definition of the POJO
     * @param field The field to access
     * @param propertyName The name of the method
     */
    private void addInnerBuilderMethod(JDefinedClass builderClass, JDefinedClass c, JVar field, String propertyName) {
        JMethod builderMethod = builderClass.method(JMod.PUBLIC, builderClass, propertyName);
        JVar param = builderMethod.param(field.type(), field.name());
        JBlock body = builderMethod.body();
        body.assign(JExpr.ref(JExpr.cast(c, JExpr._this().ref("instance")), field), param);
        body._return(JExpr._this());
    }

    /**
     * Replaces generic type definitions with the concrete type.
     * <p />
     * For example, defined classes such as <code>VerifiableCredential</code>
     * are defined with generics. By default the Builders also understand
     * generics, however when builders inherit from other another this can cause
     * Java to become confused and also results in the build warnings such as
     * <em>"Warning: Unchecked call to 'withNameParts(List)' as a member of raw
     * type 'uk.gov.di.model.NameBuilderBase'"</em>.
     *
     * <pre>{@code
     * public class VerifiableCredential<CredentialSubjectT extends CredentialSubject >{
     *    ...
     *    protected CredentialSubjectT credentialSubject;
     *    ...
     *
     *    public static abstract class VerifiableCredentialBuilderBase {
     *      ...
     *      public VerifiableCredential.VerifiableCredentialBuilderBase withCredentialSubject(CredentialSubjectT credentialSubject) {
     *        ...
     *      }
     *   }
     * }}
     * </pre>
     *
     * The default use case of the Builders is to use concrete types such as the
     * <code>VerifiableCredentialBuilder</code> will "always" return a
     * <code>VerifiableCredential</code>. There is no case when it would be
     * returned to build another type.
     *
     * @param jDefinedClass The defined class which needs correcting. If the
     *                      base builder does not exist it is skipped.
     * @see uk.gov.di.codegen.CustomBuilderRule
     */
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

    /**
     * Iterates through the class hierarchy of the defined <code>jClass</code>
     * to see if it extends from the given defined class <code>jParent</code>.
     * @param jClass The class to examine for the parent
     * @param jParent The parent class
     * @return true if the derived class extends the derived parent.
     */
    private static boolean isDerivedFrom(JClass jClass, JClass jParent) {
        for (JClass currentParent : getJClassParentIterable(jClass)) {
            if (currentParent.equals(jParent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method which returns a {@link java.lang.Iterable} of the objects
     * class theocracy. Ultimately all {@link com.sun.codemodel.JClass} objects
     * extend from Object which is when the Iterable will stop returning
     * results.
     * @param object The class object we want the superclasses for
     * @return Iterable of the superclasses
     */
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
