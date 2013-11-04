package org.motechproject.server.web.validator;

import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.LoginMode;
import org.motechproject.server.web.form.StartupForm;
import org.motechproject.server.web.form.StartupSuggestionsForm;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.apache.activemq.util.URISupport.CompositeData;
import static org.apache.activemq.util.URISupport.isCompositeURI;
import static org.apache.activemq.util.URISupport.parseComposite;
import static org.motechproject.server.web.validator.ValidationUtils.validateEmptyOrWhitespace;

/**
 * StartupFormValidator validate user information during registration process
 */
public class StartupFormValidator implements Validator {
    private UrlValidator urlValidator;

    private MotechUserService userService;
    private ConfigurationService configurationService;

    private static final String ERROR_REQUIRED = "server.error.required.%s";
    private static final String ERROR_INVALID = "server.error.invalid.%s";
    private static final String PROVIDER_NAME = "providerName";
    private static final String PROVIDER_URL = "providerUrl";
    private static final String LOGIN_MODE = "loginMode";

    public StartupFormValidator(MotechUserService userService, ConfigurationService configurationService) {
        urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
        this.userService = userService;
        this.configurationService = configurationService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz) || StartupSuggestionsForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginMode loginModeFromConfig = configurationService.getPlatformSettings().getLoginMode();
        if (loginModeFromConfig == null || loginModeFromConfig.isOpenId()) {
            String queueUrl = "queueUrl";
            validateEmptyOrWhitespace(errors, ERROR_REQUIRED, "language", queueUrl);

            if (!errors.hasFieldErrors(queueUrl)) {
                String value = errors.getFieldValue(queueUrl).toString().replace("localhost", "127.0.0.1");
                validateQueueUrl(errors, value, queueUrl);
            }

            LoginMode loginMode = LoginMode.valueOf(errors.getFieldValue("loginMode").toString());
            if (LoginMode.REPOSITORY.equals(loginMode)) {
                validateRepository(errors);
            } else if (LoginMode.OPEN_ID.equals(loginMode)) {
                validateOpenId(errors);
            } else {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, LOGIN_MODE, String.format(ERROR_REQUIRED, LOGIN_MODE));
            }
        } else {
            validateRepository(errors);
        }
    }

    private void validateOpenId(Errors errors) {
        String providerUrl = errors.getFieldValue(PROVIDER_URL).toString();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_NAME, String.format(ERROR_REQUIRED, PROVIDER_NAME));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, PROVIDER_URL, String.format(ERROR_REQUIRED, PROVIDER_URL));

        if (errors.getFieldErrorCount(PROVIDER_URL) == 0 && !urlValidator.isValid(providerUrl)) {
            errors.rejectValue(PROVIDER_URL, String.format(ERROR_INVALID, PROVIDER_URL), null, null);
        }
    }

    private void validateRepository(Errors errors) {
        String login = errors.getFieldValue("adminLogin").toString();
        String password = errors.getFieldValue("adminPassword").toString();
        String passwordConfirm = errors.getFieldValue("adminConfirmPassword").toString();
        String adminEmail = errors.getFieldValue("adminEmail").toString();

        for (String field : Arrays.asList("adminLogin", "adminPassword", "adminConfirmPassword")) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, String.format(ERROR_REQUIRED, field));
        }

        if (errors.getFieldErrorCount(login) == 0 && userService.hasUser(login)) {
            errors.rejectValue("adminLogin", "server.error.user.exist", null, null);
        }

        if (errors.getFieldErrorCount(password) == 0 && errors.getFieldErrorCount(passwordConfirm) == 0 && !password.equals(passwordConfirm)) {
            errors.rejectValue("adminPassword", "server.error.invalid.password", null, null);
        }

        if (!EmailValidator.getInstance().isValid(adminEmail)) {
            errors.rejectValue("adminEmail", "server.error.invalid.email", null, null);
        }

        UserDto user = userService.getUserByEmail(adminEmail);
        if (user != null && !user.getUserName().equals(login)) {
            errors.rejectValue("adminEmail", "server.error.email.exist", null, null);
        }
    }

    public void validateQueueUrl(Errors errors, String value, String field) {
        try {
            URI brokerURL = new URI(value);
            if (isCompositeURI(brokerURL)) {
                CompositeData data = parseComposite(brokerURL);
                String scheme = data.getScheme();
                if (scheme != null && ("failover".equals(scheme) || "fanout".equals(scheme) || "vm".equals(scheme))) {
                    for (URI uri : data.getComponents()) {
                        validateUriContainSpecificScheme(errors, field, uri);
                    }
                } else {
                    isNotValidUri(errors, field);
                }
            } else {
                isValidUri(errors, field, value);
            }
        } catch (URISyntaxException e) {
            isNotValidUri(errors, field);
        }
    }

    private void validateCompositeDataUri(CompositeData data, Errors errors, String field) {
        for (URI uri : data.getComponents()) {
            isValidUri(errors, field, uri.toString());
        }
    }

    private void isValidUri(Errors errors, String field, String uri) {
        if (errors.getFieldErrorCount(field) == 0 && !urlValidator.isValid(uri)) {
            isNotValidUri(errors, field);
        }
    }

    private void isNotValidUri(Errors errors, String field) {
        errors.rejectValue(field, String.format(ERROR_INVALID, field), null, null);
    }

    private void validateUriContainSpecificScheme(Errors errors, String field, URI uri) {
        String scheme = uri.getScheme();
        if (scheme != null && ("static".equals(scheme) || "broker".equals(scheme))) {
            if (isCompositeURI(uri)) {
                try {
                    CompositeData data = parseComposite(uri);
                    validateCompositeDataUri(data, errors, field);
                } catch (URISyntaxException e) {
                    isNotValidUri(errors, field);
                }
            } else {
                isValidUri(errors, field, uri.toString());
            }
        } else {
            isValidUri(errors, field, uri.toString());
        }
    }
}
