package org.motechproject.security.validator.impl;

import org.motechproject.security.validator.ValidatorNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Validates that there is at least one uppercase and one lowercase character
 * in the password.
 */
@Service("lowerUpperCaseValidator")
public class LowerUpperCaseValidator extends MotechPasswordValidator {

    @Autowired
    public LowerUpperCaseValidator(MessageSource messageSource) {
        super(1, 1, 0, 0, ValidatorNames.LOWERCASE_UPPERCASE, messageSource);
    }
}
