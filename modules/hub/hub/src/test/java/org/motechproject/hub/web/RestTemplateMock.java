package org.motechproject.hub.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * This class is to return the randomly generated UUID at runtime for positive scenario during subscriber's intent verification
 * @author Anuranjan
 *
 */
public class RestTemplateMock extends RestTemplate{
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
			HttpEntity<?> requestEntity, Class<T> responseType,
			Object... uriVariables) throws RestClientException {
		String uuid  = (String) uriVariables[2];
		return new ResponseEntity(uuid, HttpStatus.OK);
	}
}
