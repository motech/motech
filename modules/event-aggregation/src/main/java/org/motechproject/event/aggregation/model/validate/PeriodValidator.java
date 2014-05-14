package org.motechproject.event.aggregation.model.validate;


import org.motechproject.commons.date.ParseException;
import org.motechproject.commons.date.util.JodaFormatter;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PeriodValidator implements ConstraintValidator<ValidPeriod, String> {

    @Override
    public void initialize(ValidPeriod validPeriod) {
    }

    @Override
    public boolean isValid(String periodString, ConstraintValidatorContext constraintValidatorContext) {
        try {
            new JodaFormatter().parsePeriod(periodString);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
