package org.motechproject.hub.web;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.service.impl.SubscriptionServiceImpl;
//import org.motechproject.hub.repository.SubscriptionRepository;
//import org.motechproject.hub.repository.SubscriptionStatusRepository;
import org.motechproject.hub.util.HubUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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

	private final static Logger LOGGER = Logger
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

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private HubSubscriptionMDSService hubSubscriptionMDSService;
	//
	// private SubscriptionStatusRepository subscriptionStatusRepo;

	private RestTemplate restTemplate;

	public IntentVerificationThreadRunnable(
			HubSubscriptionMDSService hubSubscriptionMDSService,
			RestTemplate restTemplate) {
		this.hubSubscriptionMDSService = hubSubscriptionMDSService;
		this.restTemplate = restTemplate;
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
		String contentVerificationUrl = callbackUrl
				+ INTENT_VERIFICATION_PARAMS;
		try {
			ResponseEntity<String> response = restTemplate.exchange(
					contentVerificationUrl, HttpMethod.GET, entity,
					String.class, mode, topic, uuid);

			// Any status code other than 2xx is invalid response. Also, the
			// response body should match the uuid string passed in the GET call
			if (response == null || response.getStatusCode().value() / 100 != 2
					|| response.getBody() == null
					|| !response.getBody().toString().equals(uuid)) {
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
				.findSubByTopicId(topic);
		if (subscriptions == null || subscriptions.size() == 0
				|| subscriptions.size() > 1) {
			LOGGER.info("not handled earlier, need to check");
			return;
		}
		HubSubscription subscription = subscriptions.get(0);
		// TODO mds not
		// supportedhubSubscriptionMDSService.findByCallbackUrlAndTopicUrl(callbackUrl,
		// topic);
		String hubSubscriptionStatus = subscription
				.getHubSubscriptionStatusId();
		String currentStatus = null;
		if (hubSubscriptionStatus != null) {
			currentStatus = subscription.getHubSubscriptionStatusId();
		}

		// any failure should not affect the existing subscription status
		if (!SubscriptionStatusLookup.INTENT_VERIFIED.toString().equals(
				currentStatus)) {

			// fetch the HubSubscriptionStatus enum value
			String host = HubUtils.getNetworkHostName();
			subscription.setHubSubscriptionStatusId(String.valueOf(statusLookup
					.getId()));

			// insert a record corresponding to this subscription
			hubSubscriptionMDSService.create(subscription);
		}

	}

}
