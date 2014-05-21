package org.motechproject.hub.service.impl;



import org.motechproject.hub.service.DistributionServiceDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

/**
 * This class implements the methods in the interface <code>DistributionServiceDelegate</code>
 * @author Anuranjan
 *
 */
@Service
public class DistributionServiceDelegateImpl implements	DistributionServiceDelegate {

	//@Autowired
	private RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public ResponseEntity<String> getContent(String topicUrl) {
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(topicUrl, HttpMethod.POST, entity, String.class);
		return response;
	}

	@Override
	public ResponseEntity<String> distribute(String callbackUrl, String content, MediaType contentType, String topicUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(contentType);
		headers.add("Link", "<http://localhost:8080/motech-platform-hub/hub/>; rel=\"hub\", <" + topicUrl + ">; rel=\"self\"");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		ResponseEntity<String> response = restTemplate.exchange(callbackUrl, HttpMethod.POST, entity, String.class);
		return response;
	}
}
