package org.motechproject.security.validator.impl;

import org.motechproject.security.validator.ValidatorNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Validates that there is at least one uppercase character, one lowercase character and one digit
 * in the password.
 */
@Service("lowerUpperCaseDigitValidator")
public class LowerUpperCaseDigitValidator extends MotechPasswordValidator {

    @Autowired
    public LowerUpperCaseDigitValidator(MessageSource messageSource) {
        super(1, 1, 1, 0, ValidatorNames.LOWERCASE_UPPERCASE_DIGIT, messageSource);
    }
}
