package uk.gov.di.codegen;

import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.rules.PropertyRule;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.NameHelper;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

public class CustomRuleFactory extends RuleFactory {
  private final ReflectionHelper reflectionHelper;
  private final PropertyRule propertyRule;
  private NameHelper nameHelper;

  public CustomRuleFactory() {
    super();
    this.reflectionHelper = new ReflectionHelper(this);
    this.propertyRule = new CustomPropertyRule(this);
  }

  @Override
  public Rule<JClassContainer, JType> getSchemaRule() {
    return new CustomSchemaRule(this);
  }

  @Override
  public Rule<JPackage, JType> getObjectRule() {
    return new CustomObjectRule(this, new ParcelableHelper(), reflectionHelper);
  }

  @Override
  public void setGenerationConfig(GenerationConfig generationConfig) {
    super.setGenerationConfig(generationConfig);
    this.nameHelper = new CustomNameHelper(this.getGenerationConfig());
  }

  @Override
  public NameHelper getNameHelper() {
    return this.nameHelper;
  }

  @Override
  public Rule<JDefinedClass, JDefinedClass> getPropertyRule() {
    return this.propertyRule;
  }
}
