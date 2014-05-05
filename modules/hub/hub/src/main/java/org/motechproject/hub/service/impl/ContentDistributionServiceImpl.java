package org.motechproject.hub.service.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.motechproject.hub.model.DistributionStatusLookup;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubDistributionError;
import org.motechproject.hub.model.hibernate.HubDistributionStatus;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.motechproject.hub.model.hibernate.HubSubscriberTransaction;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.repository.DistributionErrorRepository;
import org.motechproject.hub.repository.DistributionStatusRepository;
import org.motechproject.hub.repository.PublisherTransactionRepository;
import org.motechproject.hub.repository.SubscriberTransactionRepository;
import org.motechproject.hub.repository.SubscriptionRepository;
import org.motechproject.hub.repository.TopicRepository;
import org.motechproject.hub.service.ContentDistributionService;
import org.motechproject.hub.service.DistributionServiceDelegate;
import org.motechproject.hub.util.HubUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ContentDistributionServiceImpl implements	ContentDistributionService {

	//@Autowired
	private SessionFactory sessionFactory;

	//@Autowired
	private TopicRepository topicRepo;

	//@Autowired
	private SubscriptionRepository subscriptionRepo;

	//@Autowired
	private PublisherTransactionRepository publisherTransactionRepo;

	//@Autowired
	private SubscriberTransactionRepository subscriberTransactionRepo;

	//@Autowired
	private DistributionErrorRepository distributionErrorRepo;

	//@Autowired
	private DistributionStatusRepository distributionStatusRepo;

	//@Autowired
	private DistributionServiceDelegate distributionServiceDelegate;

	@Value("${max.retry.count}")
	private String maxRetryCount;

	public void setMaxRetryCount(String count)	{
		this.maxRetryCount = count;
	}
	
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

	public PublisherTransactionRepository getPublisherTransactionRepo() {
		return publisherTransactionRepo;
	}

	public void setPublisherTransactionRepo(PublisherTransactionRepository publisherTransactionRepo) {
		this.publisherTransactionRepo = publisherTransactionRepo;
	}

	public SubscriberTransactionRepository getSubscriberTransactionRepo() {
		return subscriberTransactionRepo;
	}

	public void setSubscriberTransactionRepo(
			SubscriberTransactionRepository subscriberTransactionRepo) {
		this.subscriberTransactionRepo = subscriberTransactionRepo;
	}

	public DistributionErrorRepository getDistributionErrorRepo() {
		return distributionErrorRepo;
	}

	public void setDistributionErrorRepo(
			DistributionErrorRepository distributionErrorRepo) {
		this.distributionErrorRepo = distributionErrorRepo;
	}

	public DistributionStatusRepository getDistributionStatusRepo() {
		return distributionStatusRepo;
	}

	public void setDistributionStatusRepo(
			DistributionStatusRepository distributionStatusRepo) {
		this.distributionStatusRepo = distributionStatusRepo;
	}

	public DistributionServiceDelegate getDistributionServiceDelegate() {
		return distributionServiceDelegate;
	}

	public void setDistributionServiceDelegate(
			DistributionServiceDelegate distributionServiceDelegate) {
		this.distributionServiceDelegate = distributionServiceDelegate;
	}

	public ContentDistributionServiceImpl() {

	}

	@Override
	public void distribute(String url) {
		HubTopic hubTopic = topicRepo.findByTopicUrl(url);
		if (hubTopic == null) {
			hubTopic = new HubTopic();
			hubTopic.setTopicUrl(url);
			topicRepo.setAuditFields(hubTopic);
			hubTopic.setTopicId(topicRepo.getNextKey());
			topicRepo.saveOrUpdate(hubTopic);
		}
		HubPublisherTransaction publisherTransaction = new HubPublisherTransaction();
		publisherTransaction.setHubTopic(hubTopic);
		publisherTransaction.setNotificationTime(HubUtils.getCurrentDateTime());
		publisherTransactionRepo.setAuditFields(publisherTransaction);
		publisherTransaction.setPublisherTransactionId(publisherTransactionRepo.getNextKey());
		publisherTransactionRepo.saveOrUpdate(publisherTransaction);

		// Get the content
		ResponseEntity<String> response = distributionServiceDelegate.getContent(url);
		
		// Ignore any status code other than 2xx
		if (response != null && response.getStatusCode().value() / 100 == 2) {
			String content = response.getBody();
			MediaType contentType = response.getHeaders().getContentType();
			List<HubSubscription> subscriptionList = subscriptionRepo.findByTopicUrl(url);
			for (HubSubscription subscription : subscriptionList) {
				int retryCount = 0;
				DistributionStatusLookup statusLookup = DistributionStatusLookup.FAILURE;
				HubSubscriptionStatus subscriptionStatus = subscription.getHubSubscriptionStatus();
				if (subscriptionStatus.getSubscriptionStatusCode().equals(
						SubscriptionStatusLookup.INTENT_VERIFIED.toString())) {
					//distribute the content
					String callbackUrl = subscription.getCallbackUrl();
					ResponseEntity<String> distributionResponse = null;
					do {
						distributionResponse = distributionServiceDelegate.distribute(callbackUrl, content, contentType, url);
						if (distributionResponse == null || distributionResponse.getStatusCode().value() / 100 != 2) {
							HubDistributionError error = new HubDistributionError();
							error.setHubSubscription(subscription);
							String errorDescription = "Unknown error";
							if (distributionResponse != null && distributionResponse.getBody() != null) {
								errorDescription = distributionResponse.getBody();
							}
							error.setErrorDescription(errorDescription);
							distributionErrorRepo.setAuditFields(error);
							error.setDistributionErrorId(distributionErrorRepo.getNextKey());
							distributionErrorRepo.saveOrUpdate(error);
							retryCount++;
						} else {
							statusLookup = DistributionStatusLookup.SUCCESS;
							break;
						}
					} while (retryCount <= Integer.parseInt(maxRetryCount));
					
					if (statusLookup.equals(DistributionStatusLookup.FAILURE)) {
						retryCount--;
					}
					
				}
				HubSubscriberTransaction subscriberTransaction = new HubSubscriberTransaction();
				subscriberTransaction.setHubSubscription(subscription);
				HubDistributionStatus status = distributionStatusRepo.findByStatus(statusLookup.toString());
				subscriberTransaction.setHubDistributionStatus(status);
				subscriberTransaction.setRetryCount(retryCount);
				subscriberTransaction.setContentType(contentType.toString());
				subscriberTransaction.setContent(content);
				subscriberTransactionRepo.setAuditFields(subscriberTransaction);
				subscriberTransaction.setSubscriberTransactionId(subscriberTransactionRepo.getNextKey());
				subscriberTransactionRepo.saveOrUpdate(subscriberTransaction);
			}
		}
	}
}
