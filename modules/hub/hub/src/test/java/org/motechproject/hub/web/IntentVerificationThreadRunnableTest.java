package org.motechproject.hub.web;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubSubscriptionStatus;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * This is a test class which tests the <code>run()</code> method of this
 * <code>ThreadRunnable</code>
 * 
 * @author Anuranjan
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class IntentVerificationThreadRunnableTest {

	@Mock
	private HubSubscriptionMDSService hubSubscriptionMDSService;
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private IntentVerificationThreadRunnable intentVerificationThreadRunnable = new IntentVerificationThreadRunnable(hubSubscriptionMDSService,
			restTemplate);

	private String callbackUrl;
	private String topic;
	private String mode;

	private HubSubscription subscription;
	private HubTopic hubTopic;
	private HubSubscriptionStatus subscriptionStatus;

	@Before
	public void setUp() {
		callbackUrl = "callback_url";
		topic = "topic_url";
		mode = "subscribe";
		
		intentVerificationThreadRunnable.setRestTemplate(restTemplate);
		hubTopic = new HubTopic();
		hubTopic.setTopicUrl("topic url");
		subscription = new HubSubscription();
		subscription.setCallbackUrl(callbackUrl);
		subscription.setHubTopicId(1);

		subscriptionStatus = new HubSubscriptionStatus();
		subscriptionStatus.setSubscriptionStatusCode("intent_verified");

		subscription.setHubSubscriptionStatusId(3);
		intentVerificationThreadRunnable.setCallbackUrl(callbackUrl);
		intentVerificationThreadRunnable.setTopic(topic);
		intentVerificationThreadRunnable.setMode(mode);
		List<HubSubscription> subscriptions = new ArrayList<HubSubscription>();
		subscriptions.add(subscription);
		when(hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, 1)).thenReturn(subscriptions);
//		when(subscriptionRepo.findByCallbackUrlAndTopicUrl(callbackUrl, topic)).thenReturn(subscription);
//		when(subscriptionStatusRepo.findByStatus(anyString())).thenReturn(subscriptionStatus);
	}

	@Test
	public void run() {
		restTemplate = new RestTemplateMock();
		intentVerificationThreadRunnable.setRestTemplate(restTemplate);
		intentVerificationThreadRunnable.run();
		verify(hubSubscriptionMDSService).findSubByCallbackUrlAndTopicId(anyString(), (Integer)anyObject());
	}
/*
	@SuppressWarnings("unchecked")
	@Test
	public void runInvalidResponse() {
		ResponseEntity<String> response = new ResponseEntity<String>("response body", HttpStatus.BAD_REQUEST);
		when(restTemplate.exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(),
				(Class<String>) any(), anyString(), anyString(), anyString())).thenReturn(response);

		assertArguments();
		intentVerificationThreadRunnable.run();
		
		verify(restTemplate).exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(),
				(Class<String>) any(), anyString(), anyString(), anyString());
		verify(subscriptionRepo).findByCallbackUrlAndTopicUrl(callbackUrl, topic);
		verify(subscriptionStatusRepo).findByStatus(anyString());
		verify(subscriptionRepo).setAuditFieldsForUpdate((HubSubscription) anyObject(), anyString(), (Date) anyObject());
		verify(subscriptionRepo).saveOrUpdate((HubSubscription) anyObject());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void runException() {
		when(restTemplate.exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(),
				(Class<String>) any(), anyString(), anyString(), anyString())).thenThrow(new RuntimeException());

		assertArguments();
		intentVerificationThreadRunnable.run();
		
		verify(restTemplate).exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(),
				(Class<String>) any(), anyString(), anyString(), anyString());
		verify(subscriptionRepo).findByCallbackUrlAndTopicUrl(callbackUrl, topic);
		verify(subscriptionStatusRepo).findByStatus(anyString());
		verify(subscriptionRepo).setAuditFieldsForUpdate((HubSubscription) anyObject(), anyString(), (Date) anyObject());
		verify(subscriptionRepo).saveOrUpdate((HubSubscription) anyObject());
	}
	
	private void assertArguments() {


		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(3, args.length);
				HubSubscription subscription = (HubSubscription) args[0];
				assertNotNull(subscription);
				assertEquals("callback_url", subscription.getCallbackUrl());
				HubTopic topic = subscription.getHubTopic();
				assertNotNull(topic);
				assertEquals(1, topic.getTopicId());
				assertEquals("topic url", topic.getTopicUrl());
				return null;
			}
		}).when(subscriptionRepo).setAuditFieldsForUpdate((HubSubscription) anyObject(), anyString(), (Date) anyObject());

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
				assertEquals("topic url", topic.getTopicUrl());
				return null;
			}
		}).when(subscriptionRepo).saveOrUpdate((HubSubscription) anyObject());
	}*/
}
