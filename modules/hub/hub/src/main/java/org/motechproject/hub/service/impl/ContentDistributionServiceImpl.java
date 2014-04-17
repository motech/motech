package org.motechproject.hub.service.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.motechproject.hub.model.hibernate.HubSubscriberTransaction;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.repository.PublisherTransactionRepository;
import org.motechproject.hub.repository.SubscriberTransactionRepository;
import org.motechproject.hub.repository.SubscriptionRepository;
import org.motechproject.hub.repository.TopicRepository;
import org.motechproject.hub.service.ContentDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class ContentDistributionServiceImpl implements ContentDistributionService {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private TopicRepository topicRepo;
	
	@Autowired
	private SubscriptionRepository subscriptionRepo;
	
	@Autowired
	private PublisherTransactionRepository publisherRepo;
	
	@Autowired
	private SubscriberTransactionRepository subscriberRepo;
	
	@Value("${max.retry.count}")
	private String maxRetryCount;
	
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
	
	public PublisherTransactionRepository getPublisherRepo() {
		return publisherRepo;
	}

	public void setPublisherRepo(PublisherTransactionRepository publisherRepo) {
		this.publisherRepo = publisherRepo;
	}

	public SubscriberTransactionRepository getSubscriberRepo() {
		return subscriberRepo;
	}

	public void setSubscriberRepo(SubscriberTransactionRepository subscriberRepo) {
		this.subscriberRepo = subscriberRepo;
	}

	public ContentDistributionServiceImpl() {
		
	}

	@Override
	public void distribute(String url) {
		HubTopic hubTopic = topicRepo.findByTopicUrl(url);
		//TODO if null ...create a new and insert into hubTopic
		if (hubTopic != null) {
			HubPublisherTransaction publisherTransaction = new HubPublisherTransaction();
			publisherTransaction.setPublisherTransactionId(publisherRepo.getNextKey()); 
			publisherTransaction.setHubTopic(hubTopic);
			publisherRepo.saveOrUpdate(publisherTransaction);
			List<HubSubscription> subscriptionList = subscriptionRepo.findByTopicUrl(url); 
			for (HubSubscription subsciption : subscriptionList) {
				// TODO: check the status of subscription.. proceed only if status == Intent_verified...
				long id = subscriptionRepo.getNextKey(); 
				int retryCount = 0;
				//TODO: make POST request to this subscriber (callbackUrl).. till retryCount == MAX (configurable property)
				// If successful, HubSubscriberTransaction.status = success, HubSubscriberTransaction.retryCount = retryCount => insert the record
				// Whenever a failure occurs, add an entry in the Error table with this subscription id and some error description
				// If unsuccessful after all retries, HubSubscriberTransaction.status=failure, insert the record
				
				//TODO: create repositories for the remaining entitites.
				HubSubscriberTransaction subscriberTransaction = new HubSubscriberTransaction();
				subscriberTransaction.setRetryCount(retryCount);
				subscriberTransaction.setSubscriberTransactionId(id);
				//subscriberTransaction.setHubDistributionStatus(hubDistributionStatus);
			}
		} 
	}
}
