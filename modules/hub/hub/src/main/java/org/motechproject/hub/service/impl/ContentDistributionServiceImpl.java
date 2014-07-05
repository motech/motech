package org.motechproject.hub.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.motechproject.hub.mds.HubDistributionContent;
import org.motechproject.hub.mds.HubPublisherTransaction;
import org.motechproject.hub.mds.HubSubscriberTransaction;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubDistributionContentMDSService;
import org.motechproject.hub.mds.service.HubPublisherTransactionMDSService;
import org.motechproject.hub.mds.service.HubSubscriberTransactionMDSService;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.DistributionStatusLookup;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.service.ContentDistributionService;
import org.motechproject.hub.service.DistributionServiceDelegate;
import org.motechproject.hub.util.HubConstants;
import org.motechproject.hub.util.HubUtils;
import org.motechproject.hub.web.HubController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service(value = "contentDistributionService")
public class ContentDistributionServiceImpl implements
        ContentDistributionService {

    private static final Logger LOGGER = Logger.getLogger(HubController.class);

    private HubTopicMDSService hubTopicMDSService;

    private HubPublisherTransactionMDSService hubPublisherTransactionMDSService;

    private HubSubscriptionMDSService hubSubscriptionMDSService;

    private HubSubscriberTransactionMDSService hubSubscriberTransactionMDSService;

    private HubDistributionContentMDSService hubDistributionContentMDSService;

    @Autowired
    public ContentDistributionServiceImpl(
            HubTopicMDSService hubTopicService,
            HubSubscriptionMDSService hubSubscriptionMDSService,
            HubPublisherTransactionMDSService hubPublisherTransactionMDSService,
            HubSubscriberTransactionMDSService hubSubscriberTransactionMDSService,
            HubDistributionContentMDSService hubDistributionContentMDSService) {
        this.hubTopicMDSService = hubTopicService;
        this.hubSubscriptionMDSService = hubSubscriptionMDSService;
        this.hubPublisherTransactionMDSService = hubPublisherTransactionMDSService;
        this.hubSubscriberTransactionMDSService = hubSubscriberTransactionMDSService;
        this.hubDistributionContentMDSService = hubDistributionContentMDSService;
    }

    @Autowired
    private DistributionServiceDelegate distributionServiceDelegate;

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
        long topicId = -1;
        if (hubTopics == null || hubTopics.isEmpty()) {
            LOGGER.info(String.format(
                    "No Hub topics for the url '%s'. Creating the hub topic.",
                    url));
            HubTopic hubTopic = new HubTopic();
            hubTopic.setTopicUrl(url);
            hubTopic = hubTopicMDSService.create(hubTopic);
            topicId = (long) hubTopicMDSService
                    .getDetachedField(hubTopic, "id");
        } else if (hubTopics.size() > 1) {
            LOGGER.error("Multiple hub topics for the url " + url);
        } else {
            topicId = (long) hubTopicMDSService.getDetachedField(
                    hubTopics.get(0), "id");
        }

        // Get the content
        ResponseEntity<String> response = distributionServiceDelegate
                .getContent(url);

        String content = "";
        MediaType contentType = null;
        // Ignore any status code other than 2xx
        if (response != null
                && response.getStatusCode().value() / HubConstants.HUNDRED == 2) {
            content = response.getBody();
            contentType = response.getHeaders().getContentType();
            LOGGER.debug("Content received from Publisher: " + content);
        }

        HubDistributionContent hubDistributionContent = new HubDistributionContent();
        hubDistributionContent.setContent(content);
        hubDistributionContent.setContentType(contentType == null ? ""
                : contentType.toString());
        hubDistributionContentMDSService.create(hubDistributionContent);

        final long contentId = (long) hubDistributionContentMDSService
                .getDetachedField(hubDistributionContent, "id");

        HubPublisherTransaction publisherTransaction = new HubPublisherTransaction();
        publisherTransaction.setHubTopicId(Integer.valueOf((int) topicId));
        publisherTransaction.setContentId(Integer.valueOf((int) contentId));
        publisherTransaction.setNotificationTime(new DateTime(HubUtils
                .getCurrentDateTime()));
        hubPublisherTransactionMDSService.create(publisherTransaction);

        List<HubSubscription> subscriptionList = hubSubscriptionMDSService
                .findSubByTopicId(Integer.valueOf((int) topicId));

        for (HubSubscription subscription : subscriptionList) {
            long subscriptionId = (long) hubSubscriptionMDSService
                    .getDetachedField(subscription, "id");

            DistributionStatusLookup statusLookup = DistributionStatusLookup.FAILURE;
            int subscriptionStatusId = Integer.valueOf(subscription
                    .getHubSubscriptionStatusId());
            if (subscriptionStatusId == SubscriptionStatusLookup.INTENT_VERIFIED
                    .getId()) {

                // distribute the content
                String callbackUrl = subscription.getCallbackUrl();
                distributionServiceDelegate.distribute(callbackUrl, content,
                        contentType, url);

            }
            HubSubscriberTransaction subscriberTransaction = new HubSubscriberTransaction();
            subscriberTransaction.setHubSubscriptionId(Integer
                    .valueOf((int) subscriptionId));

            subscriberTransaction.setHubDistributionStatusId(statusLookup
                    .getId());
            subscriberTransaction
                    .setContentId(Integer.valueOf((int) contentId));
            hubSubscriberTransactionMDSService.create(subscriberTransaction);

        }
    }
}
