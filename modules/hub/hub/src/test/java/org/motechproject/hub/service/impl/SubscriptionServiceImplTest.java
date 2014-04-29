package org.motechproject.hub.service.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
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
import org.motechproject.hub.exception.HubErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.model.SubscriptionStatusLookup;
import org.motechproject.hub.model.hibernate.HubSubscription;
import org.motechproject.hub.model.hibernate.HubSubscriptionStatus;
import org.motechproject.hub.model.hibernate.HubTopic;
import org.motechproject.hub.repository.SubscriptionRepository;
import org.motechproject.hub.repository.SubscriptionStatusRepository;
import org.motechproject.hub.repository.TopicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

/**
 * This class tests the method inside <code>SubscriptionServiceImpl</code> class
 * @author Anuranjan
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceImplTest {
	
	@InjectMocks
	private SubscriptionServiceImpl subscriptionServiceImpl = new SubscriptionServiceImpl();

	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private TopicRepository topicRepo;
	
	@Mock
	private SubscriptionRepository subscriptionRepo;
	
	@Mock
	private SubscriptionStatusRepository subscriptionStatusRepo;

	@Mock
	private RestTemplate restTemplate;
	
	private String callbackUrl;
	private Modes mode;
	private String topic;
	private String leaseSeconds;
	private String secret;
	
	private HubTopic hubTopic;
	private HubSubscription subscription;
	private HubSubscriptionStatus subscriptionStatus;
	private List<HubSubscription> subscriptionList;

	@Before
	public void setUp() {
		
		subscriptionServiceImpl.setSessionFactory(sessionFactory);
		subscriptionServiceImpl.setTopicRepo(topicRepo);
		subscriptionServiceImpl.setSubscriptionRepo(subscriptionRepo);
		subscriptionServiceImpl.setSubscriptionStatusRepo(subscriptionStatusRepo);
		subscriptionServiceImpl.setRestTemplate(restTemplate);
		
		callbackUrl = "callback_url";
		mode = Modes.SUBSCRIBE;
		topic = "topic_url";
		leaseSeconds = "20";
		secret = "secret";

		hubTopic = new HubTopic();
		hubTopic.setTopicId(1);
		hubTopic.setTopicUrl(topic);
		subscription = new HubSubscription();
		subscription.setCallbackUrl(callbackUrl);
		subscription.setHubTopic(hubTopic);

		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode("accepted");
		
		subscriptionList = new ArrayList<HubSubscription>();
	}
	
	/**
	 * Tests the service to subscribe to a topic already subscribed
	 * @throws HubException 
	 */
	@Test
	public void subscribeTestTopicExists() throws HubException {
		
		when(topicRepo.findByTopicUrl(topic)).thenReturn(hubTopic);
		when(subscriptionRepo.findByCallbackUrlAndTopicUrl(callbackUrl, topic)).thenReturn(subscription);
		
		subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		
		verify(topicRepo).findByTopicUrl(topic);
		verify(subscriptionRepo).findByCallbackUrlAndTopicUrl(callbackUrl, topic);
	}
	
	/**
	 * Test the service to subscribe to a topic which doesnot exist
	 * @throws HubException
	 */
	@Test
	public void subscribeTestTopicDoesnotExist() throws HubException {
		subscription = null;
		when(topicRepo.findByTopicUrl(topic)).thenReturn(null);
		when(subscriptionRepo.findByCallbackUrlAndTopicUrl(callbackUrl, topic)).thenReturn(subscription);
		when(subscriptionStatusRepo.findByStatus(SubscriptionStatusLookup.ACCEPTED.toString())).thenReturn(subscriptionStatus);
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				HubTopic topic = subscription.getHubTopic();
				assertNotNull(topic);
				assertEquals("topic_url", topic.getTopicUrl());
				return null;
			}
		}).when(subscriptionRepo).setAuditFields((HubSubscription) anyObject());
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				HubTopic topic = subscription.getHubTopic();
				assertNotNull(topic);
				assertEquals("topic_url", topic.getTopicUrl());
				return null;
			}
		}).when(subscriptionRepo).saveOrUpdate((HubSubscription) anyObject());
		
		subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		
		verify(topicRepo).findByTopicUrl(topic);
		verify(subscriptionRepo).findByCallbackUrlAndTopicUrl(callbackUrl, topic);
		verify(subscriptionStatusRepo).findByStatus(SubscriptionStatusLookup.ACCEPTED.toString());
		verify(subscriptionRepo).setAuditFields((HubSubscription) anyObject());
		verify(subscriptionRepo).saveOrUpdate((HubSubscription) anyObject());
	}
	
	/**
	 * Tests the service to unsubscribe from a topic
	 * @throws HubException
	 */
	@Test
	public void unsubscribeTestTopicExists() throws HubException {
		
		mode = Modes.UNSUBSCRIBE;
		when(topicRepo.findByTopicUrl(topic)).thenReturn(hubTopic);
		when(subscriptionRepo.findByTopicUrl(topic)).thenReturn(subscriptionList);
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				HubTopic topic = subscription.getHubTopic();
				assertNotNull(topic);
				assertEquals(1, topic.getTopicId());
				assertEquals("topic_url", topic.getTopicUrl());
				return null;
			}
		}).when(subscriptionRepo).delete((HubSubscription) anyObject());
		
		try {
			subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		} catch (HubException e) {
			HubErrors he = e.getError();
			assertEquals(1003, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("Topic not found", he.getMessage());
		}
		
		verify(topicRepo).findByTopicUrl(topic);
		verify(subscriptionRepo).delete((HubSubscription) anyObject());
		verify(topicRepo, times(1)).delete(hubTopic);
	}
	
	/**
	 * Tests the service to unsubscribe from a topic which does not exist
	 */
	@Test
	public void unsubscribeTestTopicDoesnotExist() {
		
		mode = Modes.UNSUBSCRIBE;
		hubTopic = null;
		when(topicRepo.findByTopicUrl(topic)).thenReturn(hubTopic);
		
		try {
			subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		} catch (HubException e) {
			HubErrors he = e.getError();
			assertEquals(1003, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("Topic not found", he.getMessage());
		}
		
		verify(topicRepo).findByTopicUrl(topic);
	}

}
