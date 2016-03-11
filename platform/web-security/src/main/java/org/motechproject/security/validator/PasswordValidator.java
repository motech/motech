package org.motechproject.security.validator;

import org.motechproject.security.exception.PasswordValidatorException;

import java.util.Locale;

/**
 * Service interface that validates password
 */
public interface PasswordValidator {

    /**
     * Validates password.
     *
     * @param password the password to check.
     * @throws org.motechproject.security.exception.PasswordValidatorException signals an issue with the validation
     */
    void validate(String password) throws PasswordValidatorException;

    /**
     * Returns the error message for the validator. Should explain what is expected of the password.
     * The message should be treated as a literal, meaning localization is left to the validator implementation.
     * @param locale the locale for which the error message should be returned
     * @return the localized error message
     */
    String getValidationError(Locale locale);

    /**
     * Returns the name of the validator used for retrieval. Must match the value from the configuration in order
     * to be used.
     * @return the name of this validator
     */
    String getName();
}
