package org.motechproject.event.aggregation.model.validate;


import org.quartz.CronExpression;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CronValidator implements ConstraintValidator<ValidCron, String> {

    @Override
    public void initialize(ValidCron validCron) {
    }

    @Override
    public boolean isValid(String cronExpression, ConstraintValidatorContext constraintValidatorContext) {
        return CronExpression.isValidExpression(cronExpression);
    }
}
