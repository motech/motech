package org.motechproject.hub.service.impl;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * This class tests the method inside <code>DistributionServiceDelegateImpl</code> class
 * @author Anuranjan
 *
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DistributionServiceDelegateImplTest {
	
	@InjectMocks
	private DistributionServiceDelegateImpl distributionServiceDelegateImpl = new DistributionServiceDelegateImpl();

	@Mock
	private RestTemplate restTemplate;
	
	private String topicUrl;
	private String callbackUrl;
	private String content;
	private MediaType contentType;
	
	ResponseEntity<String> response;
	
	@Before
	public void setUp() {
		
		distributionServiceDelegateImpl.setRestTemplate(restTemplate);
		
		topicUrl = "topic_url";
		callbackUrl = "callback_url";
		content = "content";
		contentType = MediaType.APPLICATION_XML;
		
		response = new ResponseEntity<String>("response body", HttpStatus.OK);
		when(distributionServiceDelegateImpl.getRestTemplate().exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(), (Class<String>) any())).thenReturn(response);
	}
	
	/**
	 * Test the method to get the content of an updated topic
	 */
	@Test
	public void testGetContent() {
		ResponseEntity<String> retVal = distributionServiceDelegateImpl.getContent(topicUrl);
		
		assertNotNull(retVal);
		assertEquals("response body", retVal.getBody());
		assertEquals(HttpStatus.OK, retVal.getStatusCode());
		assertNotNull(retVal.getHeaders());
		assertEquals(0, retVal.getHeaders().size());
		
		verify(restTemplate).exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(), (Class<String>) any());
		
	}
	
	/**
	 * Tests the method to distribute the updated content of the topic to all its subscribers
	 */
	@Test
	public void testDistribute() {
		ResponseEntity<String> retVal = distributionServiceDelegateImpl.distribute(callbackUrl, content, contentType, topicUrl);
		
		assertNotNull(retVal);
		assertEquals("response body", retVal.getBody());
		assertEquals(HttpStatus.OK, retVal.getStatusCode());
		assertNotNull(retVal.getHeaders());
		assertEquals(0, retVal.getHeaders().size());
		
		verify(restTemplate).exchange(anyString(), (HttpMethod) any(), (HttpEntity<String>) anyObject(), (Class<String>) any());
	}
}
