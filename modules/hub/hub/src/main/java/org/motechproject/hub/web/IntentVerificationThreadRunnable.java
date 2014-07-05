package org.motechproject.hub.web;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.motechproject.http.agent.service.HttpAgent;
import org.motechproject.http.agent.service.Method;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.util.HubConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements <code>Runnable</code> which starts a new thread to
 * verify the intent of the subscriber requesting subscription or
 * unsubscription.
 * 
 * @author Anuranjan
 * 
 */
public class IntentVerificationThreadRunnable implements Runnable {

    private static final String INTENT_VERIFICATION_PARAMS = "?hub.mode={mode}&hub.topic={topic}&hub.challenge={challenge}";

    private String callbackUrl;
    private String mode;
    private String topic;
    private Integer topicId;
    private Integer retryCount;
    private Long retryInterval;

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Long getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Long retryInterval) {
        this.retryInterval = retryInterval;
    }

    private static final Logger LOGGER = Logger
            .getLogger(IntentVerificationThreadRunnable.class);

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    private HubSubscriptionMDSService hubSubscriptionMDSService;

    private HttpAgent httpAgentImpl;

    public IntentVerificationThreadRunnable(
            HubSubscriptionMDSService hubSubscriptionMDSService,
            HttpAgent httpAgentImpl) {
        this.hubSubscriptionMDSService = hubSubscriptionMDSService;
        this.httpAgentImpl = httpAgentImpl;
    }

    public HttpAgent getHttpAgentImpl() {
        return httpAgentImpl;
    }

    public void setHttpAgentImpl(HttpAgent httpAgentImpl) {
        this.httpAgentImpl = httpAgentImpl;
    }

    /**
     * In order to prevent an attacker from creating unwanted subscriptions on
     * behalf of a subscriber (or unsubscribing desired ones), a hub must ensure
     * that the subscriber did indeed send the subscription request. The hub
     * verifies a subscription request by sending an HTTP GET request to the
     * subscriber's callback URL as given in the subscription request.
     * 
     * For this, hub generates a random string token and sends as param
     * <code>hub.challenge</code> which the subscriber must echo in order to
     * confirm its request. Other parameters are <code>hub.mode</code> and
     * <code>hub.topic</code> coming from the subscription/unsubscription
     * request
     * 
     */
    @Override
    @Transactional
    public void run() {

        SubscriptionStatusLookup statusLookup = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>("parameters",
                headers);
        // randomly generated UUID
        String uuid = UUID.randomUUID().toString();
        String intentVerificationUrl = callbackUrl + INTENT_VERIFICATION_PARAMS;
        try {
            intentVerificationUrl = replaceParameters(intentVerificationUrl,
                    mode, topic, uuid);
            ResponseEntity<String> response = (ResponseEntity<String>) httpAgentImpl
                    .executeWithReturnTypeSync(intentVerificationUrl, entity,
                            Method.GET, retryCount, retryInterval);

            // Any status code other than 2xx is invalid response. Also, the
            // response body should match the uuid string passed in the GET call
            if (!responseValid(response, uuid)) {
                statusLookup = SubscriptionStatusLookup.INTENT_FAILED;
            } else {
                statusLookup = SubscriptionStatusLookup.INTENT_VERIFIED;
            }
        } catch (Exception e) {
            statusLookup = SubscriptionStatusLookup.INTENT_FAILED;
        }

        // fetch the subscription corresponding to the callbackUrl and the
        // topic

        List<HubSubscription> subscriptions = hubSubscriptionMDSService
                .findSubByCallbackUrlAndTopicId(callbackUrl, topicId);
        if (subscriptions == null || subscriptions.size() == 0
                || subscriptions.size() > 1) {
            LOGGER.info("not handled earlier, need to check");
            return;
        }
        HubSubscription subscription = subscriptions.get(0);

        Integer hubSubscriptionStatusId = subscription
                .getHubSubscriptionStatusId();
        Integer currentStatus = -1;
        if (hubSubscriptionStatusId != 0) {
            currentStatus = subscription.getHubSubscriptionStatusId();
        }

        // any failure should not affect the existing subscription status
        if (!SubscriptionStatusLookup.INTENT_VERIFIED.getId().equals(
                currentStatus)) {

            // fetch the HubSubscriptionStatus enum value
            subscription.setHubSubscriptionStatusId(statusLookup.getId());

            // insert a record corresponding to this subscription
            hubSubscriptionMDSService.create(subscription);
        }

    }

    private boolean responseValid(ResponseEntity<String> response, String uuid) {
        if (response == null) {
            return false;
        }
        HttpStatus status = response.getStatusCode();
        if (status.value() / HubConstants.HUNDRED != 2) {
            return false;
        }
        String responseBody = response.getBody();
        if (responseBody == null) {
            return false;
        }
        if (!responseBody.trim().equals(uuid)) {
            return false;
        }
        return true;
    }

    private String replaceParameters(String contentVerificationUrl,
            String modeString, String topic, String uuid) {
        String url = contentVerificationUrl;
        url = url.replace("{mode}", modeString);
        url = url.replace("{topic}", topic);
        url = url.replace("{challenge}", uuid);
        return url;
    }

}
