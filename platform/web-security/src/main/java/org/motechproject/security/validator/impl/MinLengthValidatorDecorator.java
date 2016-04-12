package org.motechproject.security.validator.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.exception.PasswordTooShortException;
import org.motechproject.security.exception.PasswordValidatorException;
import org.motechproject.security.validator.PasswordValidator;
import org.motechproject.security.validator.ValidatorNames;

import java.util.Locale;

/**
 * A decorator for password validators, adds logic for validating the minimal password
 * length.
 */
public class MinLengthValidatorDecorator implements PasswordValidator {

    private final PasswordValidator passwordValidator;
    private final int minPassLength;

    public MinLengthValidatorDecorator(PasswordValidator passwordValidator, int minPassLength) {
        this.passwordValidator = passwordValidator;
        this.minPassLength = minPassLength;
    }

    @Override
    public void validate(String password) throws PasswordValidatorException {
        if (minPassLength > StringUtils.length(password)) {
            throw new PasswordTooShortException(minPassLength);
        }
        passwordValidator.validate(password);
    }

    @Override
    public String getValidationError(Locale locale) {
        return passwordValidator.getValidationError(locale);
    }

    @Override
    public String getName() {
        return ValidatorNames.MIN_PASS_LENGTH + " " + passwordValidator.getName();
    }
}
