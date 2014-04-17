package org.motechproject.hub.service.impl;

import java.util.List;
import java.util.UUID;

import org.hibernate.SessionFactory;
import org.motechproject.hub.exception.ApplicationErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.repository.SubscriptionRepository;
import org.motechproject.hub.repository.TopicRepository;
import org.motechproject.hub.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final String CONTENT_VERIFICATION_PARAMS = "?hub.mode={mode}&hub.topic={topic}&hub.challenge={challenge}";

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private TopicRepository topicRepo;
	
	@Autowired
	private SubscriptionRepository subscriptionRepo;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public TopicRepository getTopicRepo() {
		return topicRepo;
	}

	public void setTopicRepo(TopicRepository topicRepo) {
		this.topicRepo = topicRepo;
	}
	
	public SubscriptionRepository getSubscriptionRepo() {
		return subscriptionRepo;
	}

	public void setSubscriptionRepo(SubscriptionRepository subscriptionRepo) {
		this.subscriptionRepo = subscriptionRepo;
	}
	
	public SubscriptionServiceImpl() {
		
	}

	@Override
	public void subscribe(final String callbackUrl, final Modes mode, final String topic,
			String leaseSeconds, String secret) throws HubException {
		
		//TODO: check if these should be done after intent verification.. because if the verification fails, we should not be adding data to database
		if (mode.equals(Modes.SUBSCRIBE)) {
			HubTopic hubTopic = topicRepo.findByTopicUrl(topic);
			if(hubTopic == null) {
				hubTopic = new HubTopic();
				hubTopic.setTopicUrl(topic);
				hubTopic.setTopicId(topicRepo.getNextKey());
				topicRepo.setAuditFields(hubTopic);
				topicRepo.saveOrUpdate(hubTopic);
			} 
			HubSubscription hubSubscription = subscriptionRepo.findByCallbackUrl(callbackUrl, topic); 
			if (hubSubscription != null) {
				throw new HubException(ApplicationErrors.TOPIC_ALREADY_SUBSCRIBED);
			} else {
				hubSubscription = new HubSubscription();
				hubSubscription.setSubscriptionId(subscriptionRepo.getNextKey()); 
				hubSubscription.setCallbackUrl(callbackUrl);
				hubSubscription.setHubTopic(hubTopic);
				if (!"".equals(secret)) {
					hubSubscription.setSecret(secret);
				}
				if (leaseSeconds != null && "".equals(leaseSeconds)) {
					hubSubscription.setLeaseSeconds(Long.parseLong(leaseSeconds));
				}
				subscriptionRepo.saveOrUpdate(hubSubscription);  //TODO: status will be Accepted here
			}
		} else if (mode.equals(Modes.UNSUBSCRIBE)) {
			HubTopic hubTopic= topicRepo.findByTopicUrl(topic);
			if (hubTopic == null) {
				throw new HubException(ApplicationErrors.TOPIC_NOT_FOUND);
			} else {
				HubSubscription hubSubscription = new HubSubscription();
				hubSubscription.setCallbackUrl(callbackUrl);
				hubSubscription.setHubTopic(hubTopic);
				subscriptionRepo.delete(hubSubscription);
			}
			List<HubSubscription> subscriptionList = subscriptionRepo.findByTopicUrl(topic); 
			if (subscriptionList.isEmpty()) {
				topicRepo.delete(hubTopic);
			}
		} 
		
		// TODO create this is new class and verify if its running asynchronously (as a separate thread)
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
				String uuid = UUID.randomUUID().toString();
				String contentVerificationUrl = callbackUrl + CONTENT_VERIFICATION_PARAMS;
				ResponseEntity<String> response = restTemplate.exchange(contentVerificationUrl, HttpMethod.GET, entity, String.class, mode.getMode(), topic, uuid);
				if (response == null || response.getStatusCode().value() / 100 != 2 || response.getBody() == null || !response.getBody().toString().equals(uuid)) {
					//TODO change the status to "wrong resp from  call back url" -> Intent Failed.
				} else {
					//TODO change the status to "Intent Verified"
				}
				
			}
		};
		runnable.run();
	}
}
