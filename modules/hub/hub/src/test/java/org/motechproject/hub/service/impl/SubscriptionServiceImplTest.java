package org.motechproject.hub.service.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
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
import org.motechproject.hub.exception.HubErrors;
import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.hub.model.Modes;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

/**
 * This class tests the method inside <code>SubscriptionServiceImpl</code> class
 * @author Anuranjan
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionServiceImplTest {
	
	@Mock
	private HubTopicMDSService hubTopicService;

	@Mock
	private HubSubscriptionMDSService hubSubscriptionMDSService;

	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private SubscriptionServiceImpl subscriptionServiceImpl = new SubscriptionServiceImpl(hubTopicService, hubSubscriptionMDSService);

	
	
	private String callbackUrl;
	private Modes mode;
	private String topic;
	private String leaseSeconds;
	private String secret;
	
	private HubTopic hubTopic;
	private HubSubscription subscription;
	private HubSubscriptionStatus subscriptionStatus;
	private List<HubSubscription> subscriptionList;
	private List<HubTopic> hubTopics;

	@Before
	public void setUp() {
		
		callbackUrl = "callback_url";
		mode = Modes.SUBSCRIBE;
		topic = "topic_url";
		leaseSeconds = "20";
		secret = "secret";

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
	}
	
	/**
	 * Tests the service to subscribe to a topic already subscribed
	 * @throws HubException 
	 */
	@Test
	public void subscribeTestTopicExists() throws HubException {
		
		when(hubTopicService.findByTopicUrl(topic)).thenReturn(hubTopics);
		when(hubTopicService.getDetachedField(hubTopic, "id")).thenReturn((long)1);
		when(hubSubscriptionMDSService.
					findSubByCallbackUrlAndTopicId(callbackUrl, 1)).thenReturn(subscriptionList);
		
		subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		
		verify(hubSubscriptionMDSService).
					findSubByCallbackUrlAndTopicId(callbackUrl, 1);
		verify(hubTopicService, times(2)).getDetachedField(hubTopic, "id");
		verify(hubTopicService).findByTopicUrl(topic);
	}
	
	/**
	 * Tests the service to unsubscribe from a topic
	 * @throws HubException
	 */
	@Test
	public void unsubscribeTestTopicExists() throws HubException {
		
		mode = Modes.UNSUBSCRIBE;
		when(hubTopicService.findByTopicUrl(topic)).thenReturn(hubTopics);
		when(hubTopicService.getDetachedField(hubTopic, "id")).thenReturn((long)1);
		when(hubSubscriptionMDSService.
					findSubByCallbackUrlAndTopicId(callbackUrl, 1)).thenReturn(subscriptionList);
		when(hubSubscriptionMDSService.findSubByTopicId(1)).thenReturn(new ArrayList<HubSubscription>());
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				return null;
			}
		}).when(hubSubscriptionMDSService).delete((HubSubscription) anyObject());
		
		try {
			subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		} catch (HubException e) {
			HubErrors he = e.getError();
			assertEquals(1003, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("Topic not found", he.getMessage());
		}
		
		verify(hubSubscriptionMDSService).findSubByTopicId(1);
		verify(hubTopicService).delete(hubTopic);
	}
	
	/**
	 * Tests the service to unsubscribe from a topic which does not exist
	 */
	@Test
	public void unsubscribeTestTopicDoesnotExist() {
		
		mode = Modes.UNSUBSCRIBE;
		hubTopic = null;
		when(hubTopicService.findByTopicUrl(topic)).thenReturn(null);
		try {
			subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		} catch (HubException e) {
			HubErrors he = e.getError();
			assertEquals(1003, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("Topic not found", he.getMessage());
		}
		verify(hubTopicService).findByTopicUrl(topic);
	}

	/**
	 * Test the service to subscribe to a topic which doesnot exist
	 * @throws HubException
	 */
	@Test
	public void subscribeTestTopicDoesnotExist() throws HubException {
		subscription = null;
		when(hubTopicService.findByTopicUrl(topic)).thenReturn(null);
		when(hubTopicService.getDetachedField((HubTopic)anyObject(), anyString())).thenReturn((long)1);
		when(hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, 1)).thenReturn(null);
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubTopic hubTopic = (HubTopic) args[0];
				assertNotNull(hubTopic);
				assertEquals("topic_url", hubTopic.getTopicUrl());
				return null;
			}
		}).when(hubTopicService).create((HubTopic) anyObject());
		
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				return null;
			}
		}).when(hubSubscriptionMDSService).create((HubSubscription) anyObject());
		
		subscriptionServiceImpl.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		
		verify(hubTopicService).create((HubTopic)anyObject());
	}

}
