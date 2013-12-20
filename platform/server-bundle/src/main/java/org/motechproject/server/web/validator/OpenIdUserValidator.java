package org.motechproject.server.web.validator;

import org.apache.commons.validator.UrlValidator;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static org.motechproject.server.web.form.StartupForm.PROVIDER_NAME;
import static org.motechproject.server.web.form.StartupForm.PROVIDER_URL;

/**
 * Validates presence of OpenId related field values
 * Also validates provider URL
 */
public class OpenIdUserValidator implements Validator {

    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private static final String ERROR_INVALID = "server.error.invalid.%s";
    private UrlValidator urlValidator;

    public OpenIdUserValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public void validate(Object target, Errors errors) {
        String providerUrl = errors.getFieldValue(PROVIDER_URL).toString();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_NAME, String.format(ERROR_REQUIRED, PROVIDER_NAME));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_URL, String.format(ERROR_REQUIRED, PROVIDER_URL));

        if (errors.getFieldErrorCount(PROVIDER_URL) == 0 && !urlValidator.isValid(providerUrl)) {
            errors.rejectValue(PROVIDER_URL, String.format(ERROR_INVALID, PROVIDER_URL), null, null);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz);
    }
}
