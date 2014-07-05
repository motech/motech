package org.motechproject.hub.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.motechproject.http.agent.service.HttpAgent;
import org.motechproject.http.agent.service.Method;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.util.HubConstants;
import org.motechproject.hub.web.IntentVerificationThreadRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * This is the implementation class of the interface
 * <code>SubscriptionService</code>
 * 
 * @author Anuranjan
 * 
 */
@Service(value = "subscriptionService")
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final String INTENT_VERIFICATION_PARAMS = "?hub.mode={mode}&hub.topic={topic}&hub.challenge={challenge}";

    private HubTopicMDSService hubTopicService;

    private HubSubscriptionMDSService hubSubscriptionMDSService;

    @Value("${retry.count}")
    private String retryCount;

    public void setRetryCount(String retryCount) {
        this.retryCount = retryCount;
    }

    @Value("${retry.interval}")
    private String retryInterval;

    public void setRetryInterval(String retryInterval) {
        this.retryInterval = retryInterval;
    }

    private HttpAgent httpAgentImpl;

    private static final Logger LOGGER = Logger
            .getLogger(SubscriptionServiceImpl.class);

    public HttpAgent getHttpAgentImpl() {
        return httpAgentImpl;
    }

    public void setHttpAgentImpl(HttpAgent httpAgentImpl) {
        this.httpAgentImpl = httpAgentImpl;
    }

    @Autowired
    public SubscriptionServiceImpl(HubTopicMDSService hubTopicService,
            HubSubscriptionMDSService hubSubscriptionMDSService,
            HttpAgent httpAgentImpl) {
        this.hubTopicService = hubTopicService;
        this.hubSubscriptionMDSService = hubSubscriptionMDSService;
        this.httpAgentImpl = httpAgentImpl;

    }

    @Override
    public void subscribe(final String callbackUrl, final Modes mode,
            final String topic, String leaseSeconds, String secret)
            throws HubException {

        HubTopic hubTopic = null;
        int hubTopicId = 0;

        // fetch the HubTopic entity corresponding to the topic url requested
        List<HubTopic> hubTopics = hubTopicService.findByTopicUrl(topic);

        if (hubTopics != null && hubTopics.size() == 1) {
            hubTopic = hubTopics.get(0);
            Object topicId = hubTopicService.getDetachedField(hubTopic, "id");
            hubTopicId = (int) (long) topicId;
        }
        if (mode.equals(Modes.SUBSCRIBE)) { // subscription request

            // Create an insert a new topic if it doesnot already exist in the
            // database
            if (hubTopic == null) {
                hubTopic = new HubTopic();
                hubTopic.setTopicUrl(topic);
                hubTopicService.create(hubTopic);
            }
            final long topicId = (long) hubTopicService.getDetachedField(
                    hubTopic, "id");
            hubTopicId = (int) topicId;

            // check if the subscriber is already subscribed to the requested
            // topic. If already subscribed, any failure will leave the previous
            // status unchanged.
            List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService
                    .findSubByCallbackUrlAndTopicId(callbackUrl,
                            Integer.valueOf((int) topicId));

            // create a new subscription record
            createHubSubscription(callbackUrl, topicId, secret, leaseSeconds,
                    hubSubscriptions, topic);

            // verification of intent of the subscriber running parallelly as
            // part of a separate thread.
            IntentVerificationThreadRunnable runnable = triggerRunnable(
                    hubTopicId, topic, callbackUrl, mode);

            Thread intentVerifiationThread = new Thread(runnable);
            intentVerifiationThread.start();

        } else if (mode.equals(Modes.UNSUBSCRIBE)) { // unsubscription request
            if (hubTopic != null) {
                unsubscribe(hubTopicId, topic, callbackUrl, mode);
            } else {
                throw new HubException(ApplicationErrors.TOPIC_NOT_FOUND);
            }
            // if no more subscribers exists for this topic, delete it from the
            // database
            List<HubSubscription> subscriptionList = hubSubscriptionMDSService
                    .findSubByTopicId(hubTopicId);
            if (subscriptionList.isEmpty()) {
                hubTopicService.delete(hubTopic);
            }
        }

    }

    private void createHubSubscription(String callbackUrl, long topicId,
            String secret, String leaseSeconds,
            List<HubSubscription> hubSubscriptions, String topic) {
        
        if (hubSubscriptions == null || hubSubscriptions.isEmpty()) {

            HubSubscription hubSubscription = new HubSubscription();
            hubSubscription.setCallbackUrl(callbackUrl);
            hubSubscription.setHubTopicId(Integer.valueOf((int) topicId));
            hubSubscription
                    .setHubSubscriptionStatusId(SubscriptionStatusLookup.ACCEPTED
                            .getId());

            if (!("").equals(secret)) {
                hubSubscription.setSecret(secret);
            }
            if (leaseSeconds != null && !("").equals(leaseSeconds)) {
                hubSubscription.setLeaseSeconds(Integer.valueOf(leaseSeconds));
            }

            hubSubscriptionMDSService.create(hubSubscription);

        } else if (hubSubscriptions.size() > 1) {
            LOGGER.error(String
                    .format("There are multiple subscriptions for the topic %s and callback url '%s'",
                            topic, callbackUrl));
        } else {
            LOGGER.error(String
                    .format("The topic %s is already subscribed to the callback url '%s'. Starting intent verification...",
                            topic, callbackUrl));
        }
    }

    private IntentVerificationThreadRunnable triggerRunnable(int hubTopicId,
            String topic, String callbackUrl, Modes mode) {
        IntentVerificationThreadRunnable runnable = new IntentVerificationThreadRunnable(
                hubSubscriptionMDSService, httpAgentImpl);
        runnable.setMode(mode.getMode());
        runnable.setCallbackUrl(callbackUrl);
        runnable.setTopicId(hubTopicId);
        runnable.setTopic(topic);
        runnable.setRetryCount(Integer.valueOf(retryCount));
        runnable.setRetryInterval(Long.valueOf(retryInterval));
        return runnable;
    }

    private void unsubscribe(int hubTopicId, String topic, String callbackUrl,
            Modes mode) throws HubException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<String>("parameters",
                headers);
        // randomly generated UUID
        String uuid = UUID.randomUUID().toString();
        String intentVerificationUrl = callbackUrl + INTENT_VERIFICATION_PARAMS;
        String modeString = mode.getMode();
        intentVerificationUrl = replaceParameters(intentVerificationUrl,
                modeString, topic, uuid);
        ResponseEntity<String> response = (ResponseEntity<String>) httpAgentImpl
                .executeWithReturnTypeSync(intentVerificationUrl, entity,
                        Method.GET, Integer.valueOf(retryCount),
                        Long.valueOf(retryInterval));

        // Any status code other than 2xx is invalid response. Also, the
        // response body should match the uuid string passed in the GET call
        if (response != null
                && response.getStatusCode().value() / HubConstants.HUNDRED == 2
                && response.getBody() != null
                && response.getBody().toString().equals(uuid)) {
            List<HubSubscription> hubSubscriptions = (List<HubSubscription>) hubSubscriptionMDSService
                    .findSubByCallbackUrlAndTopicId(callbackUrl, hubTopicId);
            if (hubSubscriptions == null || hubSubscriptions.size() == 0) {
                throw new HubException(ApplicationErrors.SUBSCRIPTION_NOT_FOUND);
            } else if (hubSubscriptions.size() == 1) {
                hubSubscriptionMDSService.delete(hubSubscriptions.get(0));
            } else {
                LOGGER.error(String
                        .format("There are multiple subscriptions for the topic %s and callback url '%s'",
                                topic, callbackUrl));
            }
        }
    }

    private String replaceParameters(String contentVerificationUrl,
            String modeString, String topic, String uuid) {
        String url = contentVerificationUrl;
        url = url.replace("{mode}", modeString);
        url = url.replace("{topic}", topic);
        url = url.replace("{challenge}", uuid);
        return url;
    }

    @Override
    public String sayHello() {
        HubTopic hubTopic = new HubTopic("topic_url_1");
        hubTopicService.create(hubTopic);
        hubTopic.setTopicUrl("topic_url_2");
        hubTopicService.create(hubTopic);
        List<HubTopic> hubTopics = hubTopicService.retrieveAll();

        String test = testQueryonInt();

        return String.format("{\"message\":\"%s\"}",
                "Hello World " + hubTopics.size() + "," + test
                        + "testQueryOn2Params" + testQueryOn2Params());

    }

    private String testQueryonInt() {
        HubSubscription hubTopic = new HubSubscription();
        hubTopic.setHubTopicId(Integer.valueOf(1));
        hubTopic.setHubSubscriptionStatusId(Integer
                .valueOf(SubscriptionStatusLookup.ACCEPTED.getId()));
        hubTopic.setCallbackUrl("callbackurl");

        hubSubscriptionMDSService.create(hubTopic);

        List<HubSubscription> hubTopics = hubSubscriptionMDSService
                .findSubByTopicId(1);
        return "Querying in int is a success" + hubTopics.size();
    }

    private int testQueryOn2Params() {
        List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService
                .findSubByCallbackUrlAndTopicId("callbackurl", 1);
        return hubSubscriptions.size();
    }
}
