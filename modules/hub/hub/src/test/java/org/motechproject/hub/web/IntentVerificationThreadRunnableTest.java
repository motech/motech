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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.http.agent.service.HttpAgent;
import org.motechproject.http.agent.service.impl.HttpAgentImpl;
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
	private HttpAgent httpAgentImpl;
	
	@InjectMocks
	private IntentVerificationThreadRunnable intentVerificationThreadRunnable = new IntentVerificationThreadRunnable(hubSubscriptionMDSService,
			httpAgentImpl);

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
		
		intentVerificationThreadRunnable.setHttpAgentImpl(httpAgentImpl);
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
	}

	@Test
	public void run() {
		httpAgentImpl = Mockito.mock(HttpAgent.class);
		intentVerificationThreadRunnable.setHttpAgentImpl(httpAgentImpl);
		intentVerificationThreadRunnable.run();
		verify(hubSubscriptionMDSService).findSubByCallbackUrlAndTopicId(anyString(), (Integer)anyObject());
	}
}
