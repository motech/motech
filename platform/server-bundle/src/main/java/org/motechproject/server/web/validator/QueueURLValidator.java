package org.motechproject.server.web.validator;

import org.apache.activemq.util.URISupport;
import org.apache.commons.validator.UrlValidator;
import org.motechproject.server.web.form.StartupForm;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.apache.activemq.util.URISupport.isCompositeURI;
import static org.apache.activemq.util.URISupport.parseComposite;
import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;
import static org.motechproject.server.web.form.StartupForm.QUEUE_URL;

/**
 * Validates presence of Queue URL and if present whether it is in expected format or not
 */
public class QueueURLValidator implements AbstractValidator {

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
    public void validate(StartupForm target, List<String> errors) {
        if (isNullOrEmpty(target.getQueueUrl())) {
            errors.add(String.format(ERROR_REQUIRED, QUEUE_URL));
            return;
        }

        try {
            String value = target.getQueueUrl().replace("localhost", "127.0.0.1");
            URI brokerURL = new URI(value);
            if (isCompositeURI(brokerURL)) {
                URISupport.CompositeData data = parseComposite(brokerURL);
                String scheme = data.getScheme();
                if (scheme != null && ("failover".equals(scheme) || "fanout".equals(scheme) || "vm".equals(scheme))) {
                    for (URI uri : data.getComponents()) {
                        validateUriContainSpecificScheme(target, errors, uri);
                    }
                } else {
                    errors.add(String.format(ERROR_INVALID, QUEUE_URL));
                }
            } else {
                isValidUri(target, errors, value);
            }
        } catch (URISyntaxException e) {
            errors.add(String.format(ERROR_INVALID, QUEUE_URL));
        }
    }

    private void validateCompositeDataUri(StartupForm target, List<String> errors, URISupport.CompositeData data) {
        for (URI uri : data.getComponents()) {
            isValidUri(target, errors, uri.toString());
        }
    }

    private void isValidUri(StartupForm target, List<String> errors, String uri) {
        if (!isNullOrEmpty(target.getQueueUrl()) && !urlValidator.isValid(uri)) {
            errors.add(String.format(ERROR_INVALID, QUEUE_URL));
        }
    }

    private void validateUriContainSpecificScheme(StartupForm target, List<String> errors, URI uri) {
        String scheme = uri.getScheme();
        if (scheme != null && ("static".equals(scheme) || "broker".equals(scheme))) {
            if (isCompositeURI(uri)) {
                try {
                    URISupport.CompositeData data = parseComposite(uri);
                    validateCompositeDataUri(target, errors, data);
                } catch (URISyntaxException e) {
                    errors.add(String.format(ERROR_INVALID, QUEUE_URL));
                }
            } else {
                isValidUri(target, errors, uri.toString());
            }
        } else {
            isValidUri(target, errors, uri.toString());
        }
    }
}
