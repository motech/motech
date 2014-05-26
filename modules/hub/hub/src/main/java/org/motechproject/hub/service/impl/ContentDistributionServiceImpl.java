package org.motechproject.hub.service.impl;

import java.util.List;

import org.motechproject.hub.mds.HubPublisherTransaction;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.DistributionStatusLookup;
import org.motechproject.hub.model.SubscriptionStatusLookup;
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


	private HubTopicMDSService hubTopicMDSService;

	public HubTopicMDSService getHubTopicService() {
		return hubTopicMDSService;
	}

	public void setHubTopicService(HubTopicMDSService hubTopicService) {
		this.hubTopicMDSService = hubTopicService;
	}
	
	
	@Autowired
	private DistributionServiceDelegate distributionServiceDelegate;

	@Value("${max.retry.count}")
	private String maxRetryCount;

	public void setMaxRetryCount(String count)	{
		this.maxRetryCount = count;
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
		List<HubTopic> hubTopics = hubTopicMDSService.findByTopicUrl(url);
		int topicId = -1;
		if (hubTopics == null) {
			HubTopic hubTopic = new HubTopic();
			hubTopic.setTopicUrl(url);
			hubTopic = hubTopicMDSService.create(hubTopic);
			//hubTopicMDSService.getId(hubTopic);
			
		}
		
//		HubPublisherTransaction publisherTransaction = new HubPublisherTransaction();
//		publisherTransaction.setHubTopicId(hubTopicId);
//		publisherTransaction.setNotificationTime(HubUtils.getCurrentDateTime());
//		publisherTransactionRepo.setAuditFields(publisherTransaction);
//		publisherTransaction.setPublisherTransactionId(publisherTransactionRepo.getNextKey());
//		publisherTransactionRepo.saveOrUpdate(publisherTransaction);

//		// Get the content
//		ResponseEntity<String> response = distributionServiceDelegate.getContent(url);
//		
//		// Ignore any status code other than 2xx
//		if (response != null && response.getStatusCode().value() / 100 == 2) {
//			String content = response.getBody();
//			MediaType contentType = response.getHeaders().getContentType();
//			List<HubSubscription> subscriptionList = subscriptionRepo.findByTopicUrl(url);
//			for (HubSubscription subscription : subscriptionList) {
//				int retryCount = 0;
//				DistributionStatusLookup statusLookup = DistributionStatusLookup.FAILURE;
//				HubSubscriptionStatus subscriptionStatus = subscription.getHubSubscriptionStatus();
//				if (subscriptionStatus.getSubscriptionStatusCode().equals(
//						SubscriptionStatusLookup.INTENT_VERIFIED.toString())) {
//					//distribute the content
//					String callbackUrl = subscription.getCallbackUrl();
//					ResponseEntity<String> distributionResponse = null;
//					do {
//						distributionResponse = distributionServiceDelegate.distribute(callbackUrl, content, contentType, url);
//						if (distributionResponse == null || distributionResponse.getStatusCode().value() / 100 != 2) {
//							HubDistributionError error = new HubDistributionError();
//							error.setHubSubscription(subscription);
//							String errorDescription = "Unknown error";
//							if (distributionResponse != null && distributionResponse.getBody() != null) {
//								errorDescription = distributionResponse.getBody();
//							}
//							error.setErrorDescription(errorDescription);
//							distributionErrorRepo.setAuditFields(error);
//							error.setDistributionErrorId(distributionErrorRepo.getNextKey());
//							distributionErrorRepo.saveOrUpdate(error);
//							retryCount++;
//						} else {
//							statusLookup = DistributionStatusLookup.SUCCESS;
//							break;
//						}
//					} while (retryCount <= Integer.parseInt(maxRetryCount));
//					
//					if (statusLookup.equals(DistributionStatusLookup.FAILURE)) {
//						retryCount--;
//					}
//					
//				}
//				HubSubscriberTransaction subscriberTransaction = new HubSubscriberTransaction();
//				subscriberTransaction.setHubSubscription(subscription);
//				HubDistributionStatus status = distributionStatusRepo.findByStatus(statusLookup.toString());
//				subscriberTransaction.setHubDistributionStatus(status);
//				subscriberTransaction.setRetryCount(retryCount);
//				subscriberTransaction.setContentType(contentType.toString());
//				subscriberTransaction.setContent(content);
//				subscriberTransactionRepo.setAuditFields(subscriberTransaction);
//				subscriberTransaction.setSubscriberTransactionId(subscriberTransactionRepo.getNextKey());
//				subscriberTransactionRepo.saveOrUpdate(subscriberTransaction);
//			}
//		}
	}
}
