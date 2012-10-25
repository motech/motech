package org.motechproject.event.aggregation.model.validate;

import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.rule.RuleAgent;
import org.motechproject.event.aggregation.service.AggregatedEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

public class RuleExpressionValidator implements ConstraintValidator<ValidRuleExpression, String> {

    @Override
    public void initialize(ValidRuleExpression validRuleExpression) {
    }

    @Override
    public boolean isValid(String expression, ConstraintValidatorContext constraintValidatorContext) {
        List<? extends AggregatedEvent> someDummyEvents = asList(
            new AggregatedEventRecord("", new HashMap<String, Object>(), new HashMap<String, Object>())
        );
        try {
            new RuleAgent(expression, someDummyEvents).execute();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
