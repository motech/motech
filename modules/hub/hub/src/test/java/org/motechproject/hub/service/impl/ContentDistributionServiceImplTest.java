package org.motechproject.hub.service.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.hub.mds.HubDistributionContent;
import org.motechproject.hub.mds.HubDistributionError;
import org.motechproject.hub.mds.HubPublisherTransaction;
import org.motechproject.hub.mds.HubSubscriberTransaction;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubDistributionContentMDSService;
import org.motechproject.hub.mds.service.HubDistributionErrorMDSService;
import org.motechproject.hub.mds.service.HubPublisherTransactionMDSService;
import org.motechproject.hub.mds.service.HubSubscriberTransactionMDSService;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.service.DistributionServiceDelegate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * This class tests the method inside <code>ContentDistributionServiceImpl</code> class
 * @author Anuranjan
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentDistributionServiceImplTest {

	
	@Mock
	private HubTopicMDSService hubTopicService;

	@Mock
	private HubDistributionErrorMDSService distributionErrorMDSService;

	@Mock
	private HubPublisherTransactionMDSService hubPublisherTransactionMDSService;

	@Mock
	private HubSubscriptionMDSService hubSubscriptionMDSService;

	@Mock
	private HubSubscriberTransactionMDSService hubSubscriberTransactionMDSService;

	@Mock
	private HubDistributionContentMDSService hubDistributionContentMDSService;
	
	@Mock
	private DistributionServiceDelegate distributionServiceDelegate;

	@Mock
	private ResponseEntity<String> response;

	@Mock
	private HttpHeaders headers;
	
	@InjectMocks
	private ContentDistributionServiceImpl contentDistributionServiceImpl = new ContentDistributionServiceImpl(hubTopicService,
			hubSubscriptionMDSService,
			distributionErrorMDSService,
			hubPublisherTransactionMDSService,
			hubSubscriberTransactionMDSService,
			hubDistributionContentMDSService);

	private String callbackUrl;
	private Modes mode;
	private String topic;
	private String leaseSeconds;
	private String secret;
	private String maxRetryCount;
	private String url;
	
	private HubTopic hubTopic;
	private HubSubscription subscription;
	private HubSubscriptionStatus subscriptionStatus;
	private List<HubSubscription> subscriptionList;
	private List<HubTopic> hubTopics;
	
	@Before
	public void setup() {
		
		callbackUrl = "callback_url";
		topic = "topic_url";
		maxRetryCount = "0";
		url = "url";

		contentDistributionServiceImpl.setDistributionServiceDelegate(distributionServiceDelegate);
		contentDistributionServiceImpl.setMaxRetryCount(maxRetryCount);
		
		hubTopic = new HubTopic();
		hubTopic.setTopicUrl(topic);
		
		subscription = new HubSubscription();
		subscription.setCallbackUrl(callbackUrl);
		subscription.setHubSubscriptionStatusId(3);
		subscription.setHubTopicId(1);
		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode("accepted");
		
		subscriptionList = new ArrayList<HubSubscription>();
		subscriptionList.add(subscription);
		hubTopics = new ArrayList<HubTopic>();
		hubTopics.add(hubTopic);
		
		when (response.getHeaders()).thenReturn(headers);
		when (response.getStatusCode()).thenReturn(HttpStatus.OK);
		when (response.getBody()).thenReturn("content");
		when (headers.getContentType()).thenReturn(MediaType.APPLICATION_XML);
		
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Subscription status = ACCEPTED
	 */
	@Test
	public void testDistributeWitValidResponseEntityAccepted(){
		when(hubTopicService.findByTopicUrl((String)any())).thenReturn(hubTopics);
		when(hubTopicService.getDetachedField(hubTopic, "id")).thenReturn((long)1);
		when(hubDistributionContentMDSService.getDetachedField((HubDistributionContent) any(), anyString())).thenReturn((long)1);
		when(hubSubscriberTransactionMDSService.getDetachedField((HubSubscriberTransaction)any(), (String)any())).thenReturn((long)1);
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(subscriptionList);
		when(hubSubscriptionMDSService
						.getDetachedField(subscription, "id")).thenReturn((long) 1);
		when(distributionServiceDelegate
								.distribute(anyString(), anyString(), (MediaType)any(),
										anyString())).thenReturn(response);
		
		contentDistributionServiceImpl.distribute(url);
		
		verify(hubTopicService, times(0)).create((HubTopic)any()); 
		verify(hubPublisherTransactionMDSService, times(1)).create((HubPublisherTransaction)any()); 
		verify(hubSubscriberTransactionMDSService).create((HubSubscriberTransaction)any());
		verify(hubDistributionContentMDSService).getDetachedField((HubDistributionContent)any(), (String)any());
		verify(hubSubscriberTransactionMDSService).getDetachedField((HubSubscriberTransaction)any(), (String)any());
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Distribution response = null
	 * Subscription status = INTENT_VERIFIED
	 */
	@Test
	public void testDistributeWitValidResponseEntityIntentVerifiedDistributionResponseNull(){

		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.INTENT_VERIFIED.toString());
		when(hubDistributionContentMDSService.getDetachedField((HubDistributionContent) any(), anyString())).thenReturn((long)1);
		when(hubSubscriberTransactionMDSService.getDetachedField((HubSubscriberTransaction)any(), (String)any())).thenReturn((long)1);
		when(hubTopicService.findByTopicUrl((String)any())).thenReturn(hubTopics);
		when(hubTopicService.getDetachedField(hubTopic, "id")).thenReturn((long)1);
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(subscriptionList);
		when(hubSubscriptionMDSService
						.getDetachedField(subscription, "id")).thenReturn((long) 1);
		when(distributionServiceDelegate
								.distribute(anyString(), anyString(), (MediaType)any(),
										anyString())).thenReturn(null);
		
		contentDistributionServiceImpl.distribute(url);
		
		verify(hubTopicService, times(0)).create((HubTopic)any()); 
		verify(hubPublisherTransactionMDSService, times(1)).create((HubPublisherTransaction)any()); 
		verify(hubSubscriberTransactionMDSService, times(1)).create((HubSubscriberTransaction)any());  
		verify(distributionErrorMDSService, times(1)).create((HubDistributionError)any());
		verify(hubDistributionContentMDSService).getDetachedField((HubDistributionContent)any(), (String)any());
		verify(hubSubscriberTransactionMDSService).getDetachedField((HubSubscriberTransaction)any(), (String)any());
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Distribution response is non-null
	 * Subscription status = INTENT_VERIFIED
	 */
	@Test
	public void testDistributeWitValidResponseEntityIntentVerifiedDistributionResponseNonNull(){

	ResponseEntity<String> response = new ResponseEntity<String>("response body", HttpStatus.BAD_REQUEST);
	subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.INTENT_VERIFIED.toString());
	when(hubTopicService.findByTopicUrl((String)any())).thenReturn(hubTopics);
	when(hubDistributionContentMDSService.getDetachedField((HubDistributionContent)any(), (String)any())).thenReturn((long)1);
	when(hubSubscriberTransactionMDSService.getDetachedField((HubSubscriberTransaction)any(), (String)any())).thenReturn((long)1);
	when(hubTopicService.getDetachedField(hubTopic, "id")).thenReturn((long)1);
	when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
	when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(subscriptionList);
	when(hubSubscriptionMDSService
					.getDetachedField(subscription, "id")).thenReturn((long) 1);
	when(distributionServiceDelegate
							.distribute(anyString(), anyString(), (MediaType)any(),
									anyString())).thenReturn(response);
	
	contentDistributionServiceImpl.distribute(url);
	
	verify(hubTopicService, times(0)).create((HubTopic)any()); 
	verify(hubPublisherTransactionMDSService, times(1)).create((HubPublisherTransaction)any()); 
	verify(hubSubscriberTransactionMDSService).create((HubSubscriberTransaction)any()); 
	verify(hubDistributionContentMDSService).getDetachedField((HubDistributionContent)any(), (String)any());
	verify(hubSubscriberTransactionMDSService).getDetachedField((HubSubscriberTransaction)any(), (String)any());
	}
	
	/**
	 * Invalid scenario: <code>hubTopic</code> and <code>ResponseEntity</code> are null.
	 */
	@Test
	public void testDistributeWithNullHubTopic() {
		hubTopics = null;
		response = null;
		subscriptionList.removeAll(subscriptionList);
		when(hubTopicService.findByTopicUrl((String)any())).thenReturn(hubTopics);
		when(hubDistributionContentMDSService.getDetachedField((HubDistributionContent)any(), (String)any())).thenReturn((long)1);
		when(hubTopicService.getDetachedField((HubTopic)any(), (String)any())).thenReturn((long)1);
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(subscriptionList);
		when(hubSubscriptionMDSService
						.getDetachedField(subscription, "id")).thenReturn((long) 1);
		when(distributionServiceDelegate
								.distribute(anyString(), anyString(), (MediaType)any(),
										anyString())).thenReturn(response);
		
		contentDistributionServiceImpl.distribute(url);
		
		verify(hubTopicService, times(1)).create((HubTopic)any()); 
		verify(hubPublisherTransactionMDSService, times(1)).create((HubPublisherTransaction)any()); 
		verify(hubSubscriberTransactionMDSService, times(0)).create((HubSubscriberTransaction)any());
		verify(hubDistributionContentMDSService).getDetachedField((HubDistributionContent)any(), (String)any());
	}
	
	/**
	 * InvalidScenario: <code>ResponseEntity</code> is null.
	 */
	@Test
	public void testDistributeWithNullResponse(){
		response = null;
		when(hubDistributionContentMDSService.getDetachedField((HubDistributionContent)any(), (String)any())).thenReturn((long)1);
		when(hubSubscriberTransactionMDSService.getDetachedField((HubSubscriberTransaction)any(), (String)any())).thenReturn((long)1);
		when(hubTopicService.findByTopicUrl((String)any())).thenReturn(hubTopics);
		when(hubTopicService.getDetachedField((HubTopic)any(), (String)any())).thenReturn((long)1);
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(subscriptionList);
		when(hubSubscriptionMDSService
						.getDetachedField(subscription, "id")).thenReturn((long) 1);
		when(distributionServiceDelegate
								.distribute(anyString(), anyString(), (MediaType)any(),
										anyString())).thenReturn(response);
		
		contentDistributionServiceImpl.distribute(url);	
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubPublisherTransaction hubPublisherTransaction = (HubPublisherTransaction) args[0];
				assertNotNull(hubPublisherTransaction);
				assertEquals((Integer)1, hubPublisherTransaction.getHubTopicId());
				return null;
			}
		}).when(hubPublisherTransactionMDSService).create((HubPublisherTransaction) any());
		
		verify(hubTopicService, times(0)).create((HubTopic)any()); 
		verify(hubPublisherTransactionMDSService, times(1)).create((HubPublisherTransaction)any()); 
		verify(hubSubscriberTransactionMDSService).create((HubSubscriberTransaction)any());
		verify(hubDistributionContentMDSService).getDetachedField((HubDistributionContent)any(), (String)any());
		verify(hubSubscriberTransactionMDSService).getDetachedField((HubSubscriberTransaction)any(), (String)any());
	}
	
}
