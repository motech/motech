package org.motechproject.hub.service.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubPublisherTransaction;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.repository.DistributionErrorRepository;
import org.motechproject.hub.repository.DistributionStatusRepository;
import org.motechproject.hub.repository.PublisherTransactionRepository;
import org.motechproject.hub.repository.SubscriberTransactionRepository;
import org.motechproject.hub.repository.SubscriptionRepository;
import org.motechproject.hub.repository.TopicRepository;
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

	@InjectMocks
	private ContentDistributionServiceImpl contentDistributionServiceImpl = new ContentDistributionServiceImpl();

	@Mock
	private SessionFactory sessionFactory;

	@Mock
	private TopicRepository topicRepo;

	@Mock
	private SubscriptionRepository subscriptionRepo;

	@Mock
	private PublisherTransactionRepository publisherTransactionRepo;

	@Mock
	private SubscriberTransactionRepository subscriberTransactionRepo;

	@Mock
	private DistributionErrorRepository distributionErrorRepo;

	@Mock
	private DistributionStatusRepository distributionStatusRepo;

	@Mock
	private DistributionServiceDelegate distributionServiceDelegate;

	@Mock
	private ResponseEntity<String> response;
	
	@Mock
	private HttpHeaders headers;
	
	private HubPublisherTransaction publisherTransaction;
	private HubTopic hubTopic;
	private HubSubscription subscription;
	private List<HubSubscription> subscriptionList;
	private HubSubscriptionStatus subscriptionStatus;
	
	private String url;
	private String topic;
	private String maxRetryCount;
	
	@Before
	public void setup() {
		
		url = "url";
		topic = "topic_url";
		maxRetryCount = "0";
		
		contentDistributionServiceImpl.setSessionFactory(sessionFactory);
		contentDistributionServiceImpl.setTopicRepo(topicRepo);
		contentDistributionServiceImpl.setSubscriptionRepo(subscriptionRepo);
		contentDistributionServiceImpl.setPublisherTransactionRepo(publisherTransactionRepo);
		contentDistributionServiceImpl.setSubscriberTransactionRepo(subscriberTransactionRepo);
		contentDistributionServiceImpl.setDistributionErrorRepo(distributionErrorRepo);
		contentDistributionServiceImpl.setDistributionStatusRepo(distributionStatusRepo);
		contentDistributionServiceImpl.setDistributionServiceDelegate(distributionServiceDelegate);
		
		contentDistributionServiceImpl.setMaxRetryCount(maxRetryCount);
		
		publisherTransaction = new HubPublisherTransaction();
		publisherTransaction.setPublisherTransactionId(1);
		
		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl(topic);
		publisherTransaction.setHubTopic(hubTopic);
		
		subscription = new HubSubscription();
		subscription.setSubscriptionId(1);
		subscription.setCallbackUrl(url);
		subscription.setHubTopic(hubTopic);
		
		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.ACCEPTED.toString());
		subscription.setHubSubscriptionStatus(subscriptionStatus);
		
		subscriptionList = new ArrayList<HubSubscription>();
		subscriptionList.add(subscription);
		
		when (response.getHeaders()).thenReturn(headers);
		when (response.getStatusCode()).thenReturn(HttpStatus.OK);
		when (response.getBody()).thenReturn("error");
		when (headers.getContentType()).thenReturn(MediaType.APPLICATION_XML);
		
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Subscription status = ACCEPTED
	 */
	@Test
	public void testDistributeWitValidResponseEntityAccepted(){
		when(topicRepo.findByTopicUrl((String)any())).thenReturn(hubTopic); 
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(subscriptionRepo.findByTopicUrl((String)any())).thenReturn(subscriptionList);
		contentDistributionServiceImpl.distribute(url);
		verify(topicRepo, times(0)).setAuditFields((HubTopic)any()); 
		verify(topicRepo, times(0)).saveOrUpdate((HubTopic)any()); 
		verify(subscriptionRepo, times(1)).findByTopicUrl((String)any());  
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Distribution response = null
	 * Subscription status = INTENT_VERIFIED
	 */
	@Test
	public void testDistributeWitValidResponseEntityIntentVerifiedDistributionResponseNull(){

		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.INTENT_VERIFIED.toString());
		when(topicRepo.findByTopicUrl((String)any())).thenReturn(hubTopic); 
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(subscriptionRepo.findByTopicUrl((String)any())).thenReturn(subscriptionList);
		contentDistributionServiceImpl.distribute(url);
		verify(topicRepo, times(0)).setAuditFields((HubTopic)any()); 
		verify(topicRepo, times(0)).saveOrUpdate((HubTopic)any()); 
		verify(subscriptionRepo, times(1)).findByTopicUrl((String)any());  
	}
	
	/**
	 * Valid scenario: <code>hubTopic</code>and <code>ResponseEntity</code> are both not null. 
	 * Distribution response is non-null
	 * Subscription status = INTENT_VERIFIED
	 */
	@Test
	public void testDistributeWitValidResponseEntityIntentVerifiedDistributionResponseNonNull(){

		subscriptionStatus.setSubscriptionStatusCode(SubscriptionStatusLookup.INTENT_VERIFIED.toString());

		when(topicRepo.findByTopicUrl((String)any())).thenReturn(hubTopic); 
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);
		when(subscriptionRepo.findByTopicUrl((String)any())).thenReturn(subscriptionList);
		when(distributionServiceDelegate.distribute((String) any(), (String) any(), (MediaType) any(), (String) any())).thenReturn(response);
		contentDistributionServiceImpl.distribute(url);
		verify(topicRepo, times(0)).setAuditFields((HubTopic)any()); 
		verify(topicRepo, times(0)).saveOrUpdate((HubTopic)any()); 
		verify(subscriptionRepo).findByTopicUrl((String)any());  
	}
	
	/**
	 * Invalid scenario: <code>hubTopic</code> and <code>ResponseEntity</code> are null.
	 */
	@Test
	public void testDistributeWithNullHubTopic() {
		hubTopic = null;
		response = null;
		when(topicRepo.findByTopicUrl((String)any())).thenReturn(hubTopic); 
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);	
		contentDistributionServiceImpl.distribute(url);
		verify(topicRepo).setAuditFields((HubTopic)any());   
		verify(topicRepo).saveOrUpdate((HubTopic)any());  
		verify(publisherTransactionRepo).setAuditFields((HubPublisherTransaction) any());
		verify(publisherTransactionRepo).getNextKey();
		verify(publisherTransactionRepo).saveOrUpdate((HubPublisherTransaction) any());
		verify(subscriptionRepo, times(0)).findByTopicUrl((String)any()); 
	}
	
	/**
	 * InvalidScenario: <code>ResponseEntity</code> is null.
	 */
	@Test
	public void testDistributeWithNotNullHubTopic(){
		response = null;
		when(topicRepo.findByTopicUrl((String)any())).thenReturn(hubTopic); 
		when(distributionServiceDelegate.getContent((String)any())).thenReturn(response);	
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubPublisherTransaction hubPublisherTransaction = (HubPublisherTransaction) args[0];
				
				assertNotNull(hubPublisherTransaction);
				
				HubTopic hubTopic = hubPublisherTransaction.getHubTopic();
				assertNotNull(hubTopic);
				assertEquals(1, hubTopic.getTopicId());
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(publisherTransactionRepo).setAuditFields((HubPublisherTransaction) any());
		
		contentDistributionServiceImpl.distribute(url);
		verify(topicRepo, times(0)).setAuditFields((HubTopic)any());   
		verify(topicRepo, times(0)).saveOrUpdate((HubTopic)any());  
		verify(publisherTransactionRepo).setAuditFields((HubPublisherTransaction) any());
		verify(publisherTransactionRepo).getNextKey();
		verify(publisherTransactionRepo).saveOrUpdate((HubPublisherTransaction) any());
		verify(subscriptionRepo, times(0)).findByTopicUrl((String)any()); 
	}
	
}
