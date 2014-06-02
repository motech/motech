package org.motechproject.hub.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.jdo.Query;

import org.apache.log4j.Logger;
//import org.hibernate.SessionFactory;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
//import org.motechproject.hub.model.hibernate.HubTopic;
//import org.motechproject.hub.repository.SubscriptionRepository;
//import org.motechproject.hub.repository.SubscriptionStatusRepository;
//import org.motechproject.hub.repository.TopicRepository;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.util.HubUtils;
import org.motechproject.hub.web.HubController;
import org.motechproject.hub.web.IntentVerificationThreadRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * This is the implementation class of the interface
 * <code>SubscriptionService</code>
 * 
 * @author Anuranjan
 * 
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private HubTopicMDSService hubTopicService;

	private HubSubscriptionMDSService hubSubscriptionMDSService;

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
		if(hubTopics != null && hubTopics.size() != 0) {
			
				if(hubTopics.size() > 1) {
			LOGGER.error("why are there 2 topics with the same url");
			return;
		}
		else {
			hubTopic = hubTopics.get(0);
			Object topicId = hubTopicService.getDetachedField(hubTopic, "id");
			hubTopicId = (int)topicId;
		}}
		if (mode.equals(Modes.SUBSCRIBE)) { //subscription request
			//Create an insert a new topic if it doesnot already exist in the database
			if(hubTopic == null) {
				hubTopic = new HubTopic();
				hubTopic.setTopicUrl(topic);
				hubTopicService.create(hubTopic);
			} 
			final Integer topicId = (Integer)hubTopicService.getDetachedField(hubTopic, "id");
			
			// check if the subscriber is already subscribed to the requested topic. If already subscribed, any failure will leave the previous status unchanged.
			
			
			List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService.
					findSubByCallbackUrlAndTopicId(callbackUrl, topicId);
			//TODO not supported by mds.findByCallbackUrlAndTopicUrl(callbackUrl,topic); 
			HubSubscription hubSubscription = null;
			if (hubSubscriptions == null || hubSubscriptions.isEmpty()) {
				
				// create a new subscription record
				hubSubscription = new HubSubscription();
				hubSubscription.setCallbackUrl(callbackUrl);
				hubSubscription.setHubTopicId(topicId);
				hubSubscription.setHubSubscriptionStatusId(SubscriptionStatusLookup.ACCEPTED.getId());
			} else if( hubSubscriptions.size() > 1  ) {
				LOGGER.error("why are there no subscriptins with same call back url");
			} else {
				hubSubscription = hubSubscriptions.get(0);
				String host = HubUtils.getNetworkHostName();
				Date dateTime = HubUtils.getCurrentDateTime();
			}
			if (!("").equals(secret)) {
				hubSubscription.setSecret(secret);
			}
			if (leaseSeconds != null && !("").equals(leaseSeconds)) {
				hubSubscription.setLeaseSeconds(Integer.valueOf(leaseSeconds));
			}
			
			hubSubscriptionMDSService.create(hubSubscription); 
				
		} else if (mode.equals(Modes.UNSUBSCRIBE)) { //unsubscription request
			
			if (hubTopic == null) {
				throw new HubException(ApplicationErrors.TOPIC_NOT_FOUND);
			} else { // form the subscription entity and delete this subscription from the database
				HubSubscription hubSubscription = new HubSubscription();
				hubSubscription.setCallbackUrl(callbackUrl);
				hubSubscription.setHubTopicId(hubTopicId);
				hubSubscriptionMDSService.delete(hubSubscription);
			}
			// if no more subscribers exists for this topic, delete it from the database
			List<HubSubscription> subscriptionList = 
					hubSubscriptionMDSService.findSubByTopicId(hubTopicId); 
			if (subscriptionList.isEmpty()) {
				hubTopicService.delete(hubTopic);
			}
		} 
		
		// verification of intent of the subscriber running parallelly as part of a separate thread.
		IntentVerificationThreadRunnable runnable = new IntentVerificationThreadRunnable(hubSubscriptionMDSService, restTemplate);
		runnable.setMode(mode.getMode());
		runnable.setCallbackUrl(callbackUrl);
		runnable.setTopic(topic);
		Thread intentVerifiationThread = new Thread(runnable); 
		intentVerifiationThread.start(); 
		
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
		Field[] fields = HubSubscription.class.getDeclaredFields();
		Method[] methods = HubSubscription.class.getDeclaredMethods();
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
