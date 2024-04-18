package uk.gov.di.codegen;

import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JType;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;

public class CustomRuleFactory extends RuleFactory {
  @Override
  public Rule<JClassContainer, JType> getSchemaRule() {
    return new CustomSchemaRule(this);
  }
}
