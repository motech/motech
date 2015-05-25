package org.motechproject.security.validator.impl;

import org.motechproject.security.validator.ValidatorNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Validates that there is at least one uppercase character, one lowercase character, one digit and
 * one special character in the password.
 */
@Service("lowerUpperCaseDigitSpecialValidator")
public class LowerUpperCaseDigitSpecialValidator extends MotechPasswordValidator {

    @Autowired
    public LowerUpperCaseDigitSpecialValidator(MessageSource messageSource) {
        super(1, 1, 1, 1, ValidatorNames.LOWERCASE_UPPERCASE_DIGIT_SPECIAL, messageSource);
    }
}
