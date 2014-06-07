package org.motechproject.hub.web;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
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
import org.motechproject.hub.exception.RestException;
import org.motechproject.hub.model.Modes;
import org.motechproject.hub.service.ContentDistributionService;
import org.motechproject.hub.service.SubscriptionService;
import org.motechproject.hub.validation.HubValidator;
import org.springframework.http.HttpStatus;

/**
 * This is a test class which tests the APIs inside <code>HubController</code> class
 * 
 * @author Anuranjan
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class HubControllerTest {

	@InjectMocks HubController controller = new HubController();
	
	@Mock
	private HubValidator hubValidator;

	@Mock
	private SubscriptionService subscriptionService;

	@Mock
	private ContentDistributionService contentDistributionService;
	
	private List<String> errors;
	private String callbackUrl;
	private String mode;
	private String topic;
	private String leaseSeconds;
	private String secret;
	private String url;

	@Before
	public void setUp() {
		
		controller.setHubValidator(hubValidator);
		controller.setSubscriptionService(subscriptionService);
		controller.setContentDistributionService(contentDistributionService);
		
		errors = new ArrayList<String>();
		callbackUrl = "callback_url";
		mode = "subscribe";
		topic = "topic_url";
		leaseSeconds = "20";
		secret = "secret";
		url = "hub_url";
	}
	
	/**
	 * Tests the subscribe method with valid input parameters
	 * @throws HubException
	 */
	@Test
	public void testSubscribe() throws HubException {
		when(hubValidator.validateSubscription(callbackUrl, mode, topic, leaseSeconds, secret)).thenReturn(errors);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(5, args.length);
				String callbackUrl = (String) args[0];
				Modes mode = (Modes) args[1];
				String topic = (String) args[2];
				String leaseSeconds = (String) args[3];
				String secret = (String) args[4];
				assertNotNull(callbackUrl);
				assertNotNull(mode);
				assertNotNull(topic);
				assertNotNull(leaseSeconds);
				assertNotNull(secret);
				assertEquals("callback_url", callbackUrl);
				assertEquals(Modes.SUBSCRIBE, mode);
				assertEquals("topic_url", topic);
				assertEquals("20", leaseSeconds);
				assertEquals("secret", secret);
				return null;
			}
		}).when(controller.getSubscriptionService()).subscribe(callbackUrl, Modes.SUBSCRIBE, topic, leaseSeconds, secret);
		
		controller.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		
		verify(hubValidator).validateSubscription(callbackUrl, mode, topic, leaseSeconds, secret);
		verify(subscriptionService).subscribe(callbackUrl, Modes.SUBSCRIBE, topic, leaseSeconds, secret);
	}
	
	/**
	 * Tests the subscribe method with invalid input parameters
	 * @throws HubException
	 */
	@Test
	public void testSubscribeInvalidInputs() throws HubException {
		errors.add("Error string");
		when(hubValidator.validateSubscription(callbackUrl, mode, topic, leaseSeconds, secret)).thenReturn(errors);
		try {
			controller.subscribe(callbackUrl, mode, topic, leaseSeconds, secret);
		} catch (RestException e) {
			HubErrors he = e.getHubException().getError();
			assertEquals(1001, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", he.getMessage());
		}
		
		verify(hubValidator).validateSubscription(callbackUrl, mode, topic, leaseSeconds, secret);
	}
	
	/**
	 * Tests the publish method with valid input parameters
	 * @throws HubException
	 */
	@Test
	public void testPublish() throws HubException {
		when(controller.getHubValidator().validatePing(mode, url)).thenReturn(errors);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				assertNotNull(args);
				assertEquals(1, args.length);
				String url = (String) args[0];
				assertNotNull(url);
				assertEquals("hub_url", url);
				return null;
			}
		}).when(controller.getContentDistributionService()).distribute(url);
		
		controller.publish(mode, url);
		
		verify(hubValidator).validatePing(mode, url);
		verify(contentDistributionService).distribute(url);
	}
	
	/**
	 * Tests the publish method with invalid input parameters
	 * @throws HubException
	 */
	@Test
	public void testPublishInvalidInputs() throws HubException {
		errors.add("Error string");
		when(hubValidator.validatePing(mode, url)).thenReturn(errors);
		try {
			controller.publish(mode, url);
		} catch (RestException e) {
			HubErrors he = e.getHubException().getError();
			assertEquals(1001, he.getCode());
			assertEquals(HttpStatus.BAD_REQUEST, he.getHttpStatus());
			assertEquals("One or more input parameter(s) may be wrong", he.getMessage());
		}
		
		verify(hubValidator).validatePing(mode, url);
	}
}
