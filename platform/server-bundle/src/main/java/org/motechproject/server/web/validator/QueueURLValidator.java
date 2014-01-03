package org.motechproject.server.web.validator;

import org.apache.activemq.util.URISupport;
import org.apache.commons.validator.UrlValidator;
import org.motechproject.server.web.form.StartupForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.activemq.util.URISupport.isCompositeURI;
import static org.apache.activemq.util.URISupport.parseComposite;
import static org.motechproject.server.web.form.StartupForm.QUEUE_URL;
import static org.motechproject.server.web.validator.ValidationUtils.validateEmptyOrWhitespace;

/**
 * Validates presence of Queue URL and if present whether it is in expected format or not
 */
public class QueueURLValidator implements Validator {

    private static final String ERROR_INVALID = "server.error.invalid.%s";
    private static final String ERROR_REQUIRED = "server.error.required.%s";

    private UrlValidator urlValidator;

    public QueueURLValidator() {
        this(new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES));
    }

    public QueueURLValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StartupForm.class.equals(clazz);
    }

    @Override
    public void validate(Object targets, Errors errors) {
        validateEmptyOrWhitespace(errors, ERROR_REQUIRED, QUEUE_URL);

        if (errors.hasFieldErrors(QUEUE_URL)) {
            return;
        }

        String value = errors.getFieldValue(QUEUE_URL).toString().replace("localhost", "127.0.0.1");

        try {
            URI brokerURL = new URI(value);
            if (isCompositeURI(brokerURL)) {
                URISupport.CompositeData data = parseComposite(brokerURL);
                String scheme = data.getScheme();
                if (scheme != null && ("failover".equals(scheme) || "fanout".equals(scheme) || "vm".equals(scheme))) {
                    for (URI uri : data.getComponents()) {
                        validateUriContainSpecificScheme(errors, QUEUE_URL, uri);
                    }
                } else {
                    markInvalidUri(errors, QUEUE_URL);
                }
            } else {
                isValidUri(errors, QUEUE_URL, value);
            }
        } catch (URISyntaxException e) {
            markInvalidUri(errors, QUEUE_URL);
        }
    }

    private void isValidUri(Errors errors, String field, String uri) {
        if (errors.getFieldErrorCount(field) == 0 && !urlValidator.isValid(uri)) {
            markInvalidUri(errors, field);
        }
    }

    private void markInvalidUri(Errors errors, String field) {
        errors.rejectValue(field, String.format(ERROR_INVALID, field), null, null);
    }

    private void validateUriContainSpecificScheme(Errors errors, String field, URI uri) {
        String scheme = uri.getScheme();
        if (scheme != null && ("static".equals(scheme) || "broker".equals(scheme))) {
            if (isCompositeURI(uri)) {
                try {
                    URISupport.CompositeData data = parseComposite(uri);
                    validateCompositeDataUri(data, errors, field);
                } catch (URISyntaxException e) {
                    markInvalidUri(errors, field);
                }
            } else {
                isValidUri(errors, field, uri.toString());
            }
        } else {
            isValidUri(errors, field, uri.toString());
        }
    }

    private void validateCompositeDataUri(URISupport.CompositeData data, Errors errors, String field) {
        for (URI uri : data.getComponents()) {
            isValidUri(errors, field, uri.toString());
        }
    }

}
