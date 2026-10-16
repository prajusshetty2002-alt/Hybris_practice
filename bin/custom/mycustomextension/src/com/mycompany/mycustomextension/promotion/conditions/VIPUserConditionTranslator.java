package com.mycompany.mycustomextension.promotion.conditions;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;

public class VIPUserConditionTranslator implements RuleConditionTranslator
{
    private static final String VIP_USER_ATTRIBUTE = "vipUser";

    @Override
    public RuleIrCondition translate(
            final RuleCompilerContext context,
            final RuleConditionData condition,
            final RuleConditionDefinitionData conditionDefinition)
    {
        final RuleIrAttributeCondition vipUserCondition = new RuleIrAttributeCondition();

        vipUserCondition.setVariable(context.generateVariable(VIPUserRAO.class));
        vipUserCondition.setAttribute(VIP_USER_ATTRIBUTE);
        vipUserCondition.setOperator(RuleIrAttributeOperator.EQUAL);
        vipUserCondition.setValue(true);

        return vipUserCondition;
    }
}