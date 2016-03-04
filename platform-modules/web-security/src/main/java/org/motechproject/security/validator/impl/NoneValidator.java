package org.motechproject.security.validator.impl;

import org.motechproject.security.validator.ValidatorNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * The implementation of the password validator that does no validation.
 */
@Service("noneValidator")
public class NoneValidator extends MotechPasswordValidator {

    @Autowired
    public NoneValidator(MessageSource messageSource) {
        super(0, 0, 0, 0, ValidatorNames.NONE, messageSource);
    }
}
