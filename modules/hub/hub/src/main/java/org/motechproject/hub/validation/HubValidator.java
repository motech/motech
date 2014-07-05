package org.motechproject.hub.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.util.HubConstants;
import org.springframework.stereotype.Service;

/**
 * This class validates the input parameters for subscription and publish APIs
 * 
 * @author Anuranjan
 * 
 */
@Service
public class HubValidator {

    /**
     * Validates the input parameters for a subscription request
     * 
     * @param callbackUrl
     *            - a <code>String</code> representing the subscriber's callback
     *            URL where notifications should be delivered
     * @param mode
     *            - represents the literal <code>String</code> "subscribe" or
     *            "unsubscribe", depending on the goal of the request
     * @param topic
     *            - a <code>String</code> representing the topic URL that the
     *            subscriber wishes to subscribe to or unsubscribe from
     * @param leaseSeconds
     *            - a <code>String</code> representing the number of seconds for
     *            which the subscriber would like to have the subscription
     *            active. This is an optional parameter
     * @return an <code>ArrayList</code> of error <code>String</code>s
     *         explaining the reasons for invalid inputs, if any
     */
    public List<String> validateSubscription(String callbackUrl, String mode,
            String topic, String leaseSeconds, String secret) {

        List<String> errors = new ArrayList<String>();

        if (mode == null || mode.isEmpty()) {
            errors.add("hub.mode must be provided");
        } else {
            boolean isModeValid = true;
            Modes modes = null;
            // mode should be one of the enum values 'subscribe' or
            // 'unsubscribe'
            try {
                modes = Modes.valueOf(mode.toUpperCase());
            } catch (IllegalArgumentException e) {

                // mode provided is not one of the enums defined
                errors.add("hub.mode provided is not valid. Provided value is ["
                        + mode + "]");
                isModeValid = false;
            }
            if (isModeValid) {
                checkModeValue(modes, errors);

                if (modes.equals(Modes.SUBSCRIBE) && secret != null) {
                    checkSecretLength(secret, errors);
                }
            }
        }

        checkCallback(callbackUrl, errors);
        checkTopic(topic, errors);
        checkLeaseSeconds(leaseSeconds, errors);

        return errors;
    }

    /**
     * 
     * @param mode
     *            - represents the literal <code>String</code> "publish"
     * @param url
     *            - a <code>String</code> representing the url of the topic that
     *            was updated
     * @return an <code>ArrayList</code> of error <code>String</code>s
     *         explaining the reasons for invalid inputs, if any
     */
    public List<String> validatePing(String mode, String url) {
        List<String> errors = new ArrayList<String>();
        if (mode == null || mode.isEmpty()) {
            errors.add("hub.mode must be provided");
        } else {
            try {
                // mode should be 'publish'
                Modes modes = Modes.valueOf(mode.toUpperCase());
                if (!modes.equals(Modes.PUBLISH)) {
                    errors.add("Invalid mode type [" + mode
                            + "]. Supported mode: 'publish'");
                }
            } catch (IllegalArgumentException e) {
                errors.add("hub.mode provided is not valid. Provided value is ["
                        + mode + "]");
            }
        }
        if (url == null || url.isEmpty()) {
            errors.add("hub.url must be provided");
        }
        return errors;
    }

    private void checkModeValue(Modes modes, List<String> errors) {
        if (!modes.equals(Modes.SUBSCRIBE) && !modes.equals(Modes.UNSUBSCRIBE)) {
            errors.add("Invalid mode type [" + modes.getMode()
                    + "]. Supported modes: 'subscribe' or 'unsubscribe'");
        }
    }

    private void checkSecretLength(String secret, List<String> errors) {
        if (secret.getBytes().length > HubConstants.SECRET_LENGTH) {
            errors.add("hub.secret is too long. Maximum 200 bytes supported. Provided length is ["
                    + secret.getBytes().length + "]");
        }
    }

    private void checkCallback(String callbackUrl, List<String> errors) {
        if (callbackUrl == null || callbackUrl.isEmpty()) {
            errors.add("hub.callback must be provided");
        }
    }

    private void checkLeaseSeconds(String leaseSeconds, List<String> errors) {
        if (leaseSeconds != null && !StringUtils.isNumeric(leaseSeconds)) {
            errors.add("hub.lease_seconds must be numeric. Provided value is ["
                    + leaseSeconds + "]");
        }
    }

    private void checkTopic(String topic, List<String> errors) {
        if (topic == null || topic.isEmpty()) {
            errors.add("hub.topic must be provided");
        }
    }

}
