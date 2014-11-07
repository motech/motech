package org.motechproject.config.core.validator;

import org.apache.activemq.util.URISupport;
import org.apache.commons.validator.UrlValidator;
import org.motechproject.config.core.MotechConfigurationException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.activemq.util.URISupport.isCompositeURI;
import static org.apache.activemq.util.URISupport.parseComposite;
import static org.motechproject.commons.date.util.StringUtil.isNullOrEmpty;

/**
 * Validates presence of Queue URL and if present whether it is in expected format or not
 */
public class QueueURLValidator {

    private static final String URL_INVALID = "Queue URL is invalid.";

    private UrlValidator urlValidator;

    public QueueURLValidator() {
        this(new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES));
    }

    public QueueURLValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public void validate(String queueUrl) {
        if (isNullOrEmpty(queueUrl)) {
            throw new MotechConfigurationException("Queue URL cannot be null or empty.");
        }

        try {
            String value = queueUrl.replace("localhost", "127.0.0.1");
            URI brokerURL = new URI(value);
            if (isCompositeURI(brokerURL)) {
                URISupport.CompositeData data = parseComposite(brokerURL);
                String scheme = data.getScheme();
                if (scheme != null && ("failover".equals(scheme) || "fanout".equals(scheme) || "vm".equals(scheme))) {
                    for (URI uri : data.getComponents()) {
                        validateUriContainSpecificScheme(queueUrl, uri);
                    }
                } else {
                    throw new MotechConfigurationException(URL_INVALID);
                }
            } else {
                isValidUri(queueUrl, value);
            }
        } catch (URISyntaxException e) {
            throw new MotechConfigurationException(URL_INVALID, e);
        }
    }

    private void validateCompositeDataUri(String queueUrl, URISupport.CompositeData data) {
        for (URI uri : data.getComponents()) {
            isValidUri(queueUrl, uri.toString());
        }
    }

    private void isValidUri(String queueUrl, String uri) {
        if (!isNullOrEmpty(queueUrl) && !urlValidator.isValid(uri)) {
            throw new MotechConfigurationException(URL_INVALID);
        }
    }

    private void validateUriContainSpecificScheme(String queueUrl, URI uri) {
        String scheme = uri.getScheme();
        if (scheme != null && ("static".equals(scheme) || "broker".equals(scheme))) {
            if (isCompositeURI(uri)) {
                try {
                    URISupport.CompositeData data = parseComposite(uri);
                    validateCompositeDataUri(queueUrl, data);
                } catch (URISyntaxException e) {
                    throw new MotechConfigurationException(URL_INVALID, e);
                }
            } else {
                isValidUri(queueUrl, uri.toString());
            }
        } else {
            isValidUri(queueUrl, uri.toString());
        }
    }
}
