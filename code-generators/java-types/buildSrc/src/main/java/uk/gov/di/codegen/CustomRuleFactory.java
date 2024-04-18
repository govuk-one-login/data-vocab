package uk.gov.di.codegen;

import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;
import org.jsonschema2pojo.util.ReflectionHelper;

public class CustomRuleFactory extends RuleFactory {
  private final ReflectionHelper reflectionHelper;

  public CustomRuleFactory() {
    super();
    this.reflectionHelper = new ReflectionHelper(this);
  }

  @Override
  public Rule<JClassContainer, JType> getSchemaRule() {
    return new CustomSchemaRule(this);
  }

  @Override
  public Rule<JPackage, JType> getObjectRule() {
    return new CustomObjectRule(this, new ParcelableHelper(), reflectionHelper);
  }
}
