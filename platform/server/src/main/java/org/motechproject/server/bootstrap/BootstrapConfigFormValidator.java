package org.motechproject.server.bootstrap;

import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.motechproject.server.web.validator.ValidationUtils.validateEmptyOrWhitespace;

/**
 * Validator that validates bootstrap configuration input by the user in the bootstrap config UI.
 */
public class BootstrapConfigFormValidator implements Validator {
    static final String ERROR_REQUIRED = "server.error.required.%s";

    @Override
    public boolean supports(Class<?> clazz) {
        return BootstrapConfigForm.class.equals(clazz) || PlatformStatus.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // we don't care about validating this, but has to be included in supports
        if (target instanceof PlatformStatus) {
            return;
        }

        String dbUrlField = "sqlUrl";
        String dbDriverField = "sqlDriver";

        validateEmptyOrWhitespace(errors, ERROR_REQUIRED, dbUrlField);
        validateEmptyOrWhitespace(errors, ERROR_REQUIRED, dbDriverField);

        String configSource = ((BootstrapConfigForm) target).getConfigSource();
        if (!ConfigSource.isValid(configSource)) {
            errors.rejectValue(configSource, "server.error.invalid.configSource");
        }
    }
}
