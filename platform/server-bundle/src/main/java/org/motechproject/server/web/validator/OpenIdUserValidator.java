package org.motechproject.server.web.validator;

import org.apache.commons.validator.UrlValidator;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.server.web.form.StartupForm;

import java.util.List;

import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;
import static org.motechproject.server.web.form.StartupForm.PROVIDER_NAME;
import static org.motechproject.server.web.form.StartupForm.PROVIDER_URL;

/**
 * Validates presence of OpenId related field values
 * Also validates provider URL
 */
public class OpenIdUserValidator implements AbstractValidator {

    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private static final String ERROR_INVALID = "server.error.invalid.%s";
    private UrlValidator urlValidator;

    public OpenIdUserValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public void validate(StartupForm target, List<String> errors, ConfigSource configSource) {

        if (isNullOrEmpty(target.getProviderName())) {
            if (!configSource.isFile()) {
                errors.add(String.format(ERROR_REQUIRED, PROVIDER_NAME));
            }
        }

        if (isNullOrEmpty(target.getProviderUrl())) {
            errors.add(String.format(ERROR_REQUIRED, PROVIDER_URL));
        } else if (!urlValidator.isValid(target.getProviderUrl())) {
            errors.add(String.format(ERROR_INVALID, PROVIDER_URL));
        }
    }
}
