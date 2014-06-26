package org.motechproject.hub.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.web.IntentVerificationThreadRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

	@Autowired
	private RestTemplate restTemplate;

	private final static Logger LOGGER = Logger
			.getLogger(SubscriptionServiceImpl.class);

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	public SubscriptionServiceImpl(HubTopicMDSService hubTopicService,
			HubSubscriptionMDSService hubSubscriptionMDSService) {
		this.hubTopicService = hubTopicService;
		this.hubSubscriptionMDSService = hubSubscriptionMDSService;

	}

	@Override
	//@Transactional
	public void subscribe(final String callbackUrl, final Modes mode, final String topic,
			String leaseSeconds, String secret) throws HubException {

		// fetch the HubTopic entity corresponding to the topic url requested
		List<HubTopic> hubTopics = hubTopicService.findByTopicUrl(topic);
		HubTopic hubTopic = null;
		int hubTopicId = 0;
		if (hubTopics != null && hubTopics.size() != 0) {

			if (hubTopics.size() > 1) {
				LOGGER.error("why are there multiple topics with the same url");
				return;
			} else {
				hubTopic = hubTopics.get(0);
				Object topicId = hubTopicService.getDetachedField(hubTopic,
						"id");
				hubTopicId = (int) (long) topicId;
			}
		}
		if (mode.equals(Modes.SUBSCRIBE)) { //subscription request
			//Create an insert a new topic if it doesnot already exist in the database
			if(hubTopic == null) {
				hubTopic = new HubTopic();
				hubTopic.setTopicUrl(topic);
				hubTopicService.create(hubTopic);
			} 
			final long topicId = (long)hubTopicService.getDetachedField(hubTopic, "id");
			hubTopicId = (int)topicId;
			// check if the subscriber is already subscribed to the requested topic. If already subscribed, any failure will leave the previous status unchanged.
			
			
			List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService.
					findSubByCallbackUrlAndTopicId(callbackUrl, Integer.valueOf((int)topicId));
			
			HubSubscription hubSubscription = null;
			if (hubSubscriptions == null || hubSubscriptions.isEmpty()) {
				
				// create a new subscription record
				hubSubscription = new HubSubscription();
				hubSubscription.setCallbackUrl(callbackUrl);
				hubSubscription.setHubTopicId(Integer.valueOf((int)topicId));
				hubSubscription.setHubSubscriptionStatusId(SubscriptionStatusLookup.ACCEPTED.getId());

				if (!("").equals(secret)) {
					hubSubscription.setSecret(secret);
				}
				if (leaseSeconds != null && !("").equals(leaseSeconds)) {
					hubSubscription.setLeaseSeconds(Integer.valueOf(leaseSeconds));
				}
					
				hubSubscriptionMDSService.create(hubSubscription); 
			} else if( hubSubscriptions.size() > 1  ) {
				LOGGER.error("why are there multiple subscriptions with same call back url");
			} else {
				// only subscriber already subscribed
				//hubSubscription = hubSubscriptions.get(0);
			}
			
			// verification of intent of the subscriber running parallelly as part of a separate thread.
			IntentVerificationThreadRunnable runnable = new IntentVerificationThreadRunnable(hubSubscriptionMDSService, restTemplate);
			runnable.setMode(mode.getMode());
			runnable.setCallbackUrl(callbackUrl);
			runnable.setTopicId(hubTopicId);
			runnable.setTopic(topic);
			Thread intentVerifiationThread = new Thread(runnable); 
			intentVerifiationThread.start(); 
			
		} else if (mode.equals(Modes.UNSUBSCRIBE)) { //unsubscription request

			if (hubTopic == null) {
				throw new HubException(ApplicationErrors.TOPIC_NOT_FOUND);
			}  else {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				HttpEntity<String> entity = new HttpEntity<String>("parameters",
						headers);
				// randomly generated UUID
				String uuid = UUID.randomUUID().toString();
				String contentVerificationUrl = callbackUrl
						+ INTENT_VERIFICATION_PARAMS;
				try {
					String modeString = mode.getMode();
					ResponseEntity<String> response = restTemplate.exchange(
							contentVerificationUrl, HttpMethod.GET, entity,
							String.class, modeString, topic, uuid);
	
					// Any status code other than 2xx is invalid response. Also, the
					// response body should match the uuid string passed in the GET call
					if (response != null && response.getStatusCode().value() / 100 == 2
							&& response.getBody() != null
							&& response.getBody().toString().equals(uuid)) {
						List<HubSubscription> hubSubscriptions = (List<HubSubscription>)hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, hubTopicId);
						if (hubSubscriptions == null || hubSubscriptions.size() == 0) {
							throw new HubException(ApplicationErrors.SUBSCRIPTION_NOT_FOUND);
						} else if (hubSubscriptions.size() == 1) {
							hubSubscriptionMDSService.delete(hubSubscriptions.get(0));
						} else {
							// why are there multiple subscriptions for same callback and topic?
						}
					} 
				} catch (Exception e) {
					//
				}
			}
			
			// if no more subscribers exists for this topic, delete it from the database
			List<HubSubscription> subscriptionList = 
					hubSubscriptionMDSService.findSubByTopicId(hubTopicId); 
			if (subscriptionList.isEmpty()) {
				hubTopicService.delete(hubTopic);
			}
		} 
		
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
				"Hello World " + hubTopics.size() + "," + test + 
				"testQueryOn2Params" + testQueryOn2Params());

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
		List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService.
				findSubByCallbackUrlAndTopicId("callbackurl", 1);
		return hubSubscriptions.size();
	}
}
