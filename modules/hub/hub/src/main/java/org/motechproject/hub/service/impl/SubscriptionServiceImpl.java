package org.motechproject.hub.service.impl;

import java.util.Date;
import java.util.List;




//import org.hibernate.SessionFactory;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
//import org.motechproject.hub.model.hibernate.HubTopic;
//import org.motechproject.hub.repository.SubscriptionRepository;
//import org.motechproject.hub.repository.SubscriptionStatusRepository;
//import org.motechproject.hub.repository.TopicRepository;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.util.HubUtils;
import org.motechproject.hub.web.IntentVerificationThreadRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * This is the implementation class of the interface <code>SubscriptionService</code>
 * @author Anuranjan
 *
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private HubTopicMDSService hubTopicService;
	
//	//@Autowired
//	private SessionFactory sessionFactory;
//	
//	//@Autowired
//	private TopicRepository topicRepo;
//	
//	//@Autowired
//	private SubscriptionRepository subscriptionRepo;
//	
//	//@Autowired
//	private SubscriptionStatusRepository subscriptionStatusRepo;
//
//	//@Autowired
//	private RestTemplate restTemplate;
//	
//	public SessionFactory getSessionFactory() {
//		return sessionFactory;
//	}
//
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}
//	
//	public TopicRepository getTopicRepo() {
//		return topicRepo;
//	}
//
//	public void setTopicRepo(TopicRepository topicRepo) {
//		this.topicRepo = topicRepo;
//	}
//	
//	public SubscriptionRepository getSubscriptionRepo() {
//		return subscriptionRepo;
//	}
//
//	public void setSubscriptionRepo(SubscriptionRepository subscriptionRepo) {
//		this.subscriptionRepo = subscriptionRepo;
//	}
//	
//	public SubscriptionStatusRepository getSubscriptionStatusRepo() {
//		return subscriptionStatusRepo;
//	}
//
//	public void setSubscriptionStatusRepo(
//			SubscriptionStatusRepository subscriptionStatusRepo) {
//		this.subscriptionStatusRepo = subscriptionStatusRepo;
//	}
//	
//	public RestTemplate getRestTemplate() {
//		return restTemplate;
//	}
//
//	public void setRestTemplate(RestTemplate restTemplate) {
//		this.restTemplate = restTemplate;
//	}
//
	@Autowired
	public SubscriptionServiceImpl(HubTopicMDSService hubTopicService) {
		this.hubTopicService = hubTopicService;
	}

	@Override
	//@Transactional
	public void subscribe(final String callbackUrl, final Modes mode, final String topic,
			String leaseSeconds, String secret) throws HubException {

		// fetch the HubTopic entity corresponding to the topic url requested
//		HubTopic hubTopic = topicRepo.findByTopicUrl(topic);
//		if (mode.equals(Modes.SUBSCRIBE)) { //subscription request
//			// Create an insert a new topic if it doesnot already exist in the database
//			if(hubTopic == null) {
//				hubTopic = new HubTopic();
//				hubTopic.setTopicUrl(topic);
//				topicRepo.setAuditFields(hubTopic);
//				hubTopic.setTopicId(topicRepo.getNextKey());
//				topicRepo.saveOrUpdate(hubTopic);
//			} 
//			
//			// check if the subscriber is already subscribed to the requested topic. If already subscribed, any failure will leave the previous status unchanged.
//			HubSubscription hubSubscription = subscriptionRepo.findByCallbackUrlAndTopicUrl(callbackUrl, topic); 
//			
//			if (hubSubscription == null) {
//				
//				// create a new subscription record
//				hubSubscription = new HubSubscription();
//				hubSubscription.setCallbackUrl(callbackUrl);
//				hubSubscription.setHubTopic(hubTopic);
//				HubSubscriptionStatus status = subscriptionStatusRepo.findByStatus(SubscriptionStatusLookup.ACCEPTED.toString());
//				hubSubscription.setHubSubscriptionStatus(status);
//				subscriptionRepo.setAuditFields(hubSubscription);
//				hubSubscription.setSubscriptionId(subscriptionRepo.getNextKey()); 
//			} else {
//				String host = HubUtils.getNetworkHostName();
//				Date dateTime = HubUtils.getCurrentDateTime();
//				subscriptionRepo.setAuditFieldsForUpdate(hubSubscription, host, dateTime);
//			}
//			if (!("").equals(secret)) {
//				hubSubscription.setSecret(secret);
//			}
//			if (leaseSeconds != null && !("").equals(leaseSeconds)) {
//				hubSubscription.setLeaseSeconds(Long.parseLong(leaseSeconds));
//			}
//			
//			subscriptionRepo.saveOrUpdate(hubSubscription); 
//				
//		} else if (mode.equals(Modes.UNSUBSCRIBE)) { //unsubscription request
//			
//			if (hubTopic == null) {
//				throw new HubException(ApplicationErrors.TOPIC_NOT_FOUND);
//			} else { // form the subscription entity and delete this subscription from the database
//				HubSubscription hubSubscription = new HubSubscription();
//				hubSubscription.setCallbackUrl(callbackUrl);
//				hubSubscription.setHubTopic(hubTopic);
//				subscriptionRepo.delete(hubSubscription);
//			}
//			// if no more subscribers exists for this topic, delete it from the database
//			List<HubSubscription> subscriptionList = subscriptionRepo.findByTopicUrl(topic); 
//			if (subscriptionList.isEmpty()) {
//				topicRepo.delete(hubTopic);
//			}
//		} 
//		
//		// verification of intent of the subscriber running parallelly as part of a separate thread.
//		IntentVerificationThreadRunnable runnable = new IntentVerificationThreadRunnable(subscriptionRepo, subscriptionStatusRepo, restTemplate);
//		runnable.setMode(mode.getMode());
//		runnable.setCallbackUrl(callbackUrl);
//		runnable.setTopic(topic);
//		Thread intentVerifiationThread = new Thread(runnable); 
//		intentVerifiationThread.start(); 
		
	}

	@Override
	public String sayHello() {
		 HubTopic hubTopic = new HubTopic("topic_url_1");
		 hubTopicService.create(hubTopic);
		 hubTopic.setTopicUrl("topic_url_2");
		 hubTopicService.create(hubTopic);
		 List<HubTopic> hubTopics = hubTopicService.retrieveAll();
		 
	      return String.format("{\"message\":\"%s\"}", "Hello World " + hubTopics.size());

		
	}
}
