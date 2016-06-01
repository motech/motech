package org.motechproject.config.core.validator;

import org.apache.activemq.util.URISupport;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.motechproject.config.core.exception.MotechConfigurationException;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.activemq.util.URISupport.isCompositeURI;
import static org.apache.activemq.util.URISupport.parseComposite;

/**
 * Validates presence of Queue URL and if present whether it is in expected format or not
 */
public class QueueURLValidator {

    private static final String URL_INVALID = "Queue URL is invalid.";

    private UrlValidator urlValidator;

    public QueueURLValidator() {
        this(new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES + UrlValidator.ALLOW_LOCAL_URLS));
    }

    public QueueURLValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    /**
     * Checks whether given URL is valid.
     *
     * @param queueUrl  the URL to be validated
     * @throws MotechConfigurationException if queueUrl is null, empty or invalid
     */
    public void validate(String queueUrl) {
        if (StringUtils.isBlank(queueUrl)) {
            throw new MotechConfigurationException("Queue URL cannot be null or empty.");
        }

        try {
            String value = queueUrl.replace("localhost", "127.0.0.1");
            URI brokerURL = new URI(value);
            if (isCompositeURI(brokerURL)) {
                validateComposite(brokerURL, value);
            } else {
                isValidUri(queueUrl, value);
            }
        } catch (URISyntaxException e) {
            throw new MotechConfigurationException(URL_INVALID, e);
        }
    }

    private void validateComposite(URI brokerURL, String value) throws URISyntaxException {
        URISupport.CompositeData data = parseComposite(brokerURL);
        String scheme = data.getScheme();
        if (scheme != null && ("failover".equals(scheme) || "fanout".equals(scheme) || "vm".equals(scheme))) {
            for (URI uri : data.getComponents()) {
                validateUriContainSpecificScheme(brokerURL.toString(), uri);
            }
        } else if (scheme != null) {
            isValidUri(brokerURL.toString(), value);
        } else {
            throw new MotechConfigurationException(URL_INVALID);
        }
    }

    private void validateCompositeDataUri(String queueUrl, URISupport.CompositeData data) {
        for (URI uri : data.getComponents()) {
            isValidUri(queueUrl, uri.toString());
        }
    }

    private void isValidUri(String queueUrl, String uri) {
        if (StringUtils.isNotBlank(queueUrl) && !urlValidator.isValid(uri)) {
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
