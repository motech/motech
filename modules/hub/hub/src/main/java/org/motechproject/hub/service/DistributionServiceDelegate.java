package org.motechproject.hub.service;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * This is an interface providing methods to get the Content of an updated topic and distribute it to all the subscribers for that topic
 * @author Anuranjan
 *
 */
public interface DistributionServiceDelegate {

	/**
	 * Fetches the content from a publisher corresponding to the <code>topicUrl</code>
	 * @param topicUrl - a <code>String</code> representing the topic URL which is updated
	 * @return
	 */
	public ResponseEntity<String> getContent(String topicUrl);
	
	/**
	 * Distributes the fetched content to all the subsribers subscribed to the particular topic
	 * @param callbackUrl - a <code>String</code> representing the subscriber's callback URL where notifications should be delivered
	 * @param content - a <code>String</code> representing the full content of the updated topic
	 * @param contentType - a <code>String</code> representing the <code>Content-Type</code> of the topic updated
	 * @param topicUrl -  a <code>String</code> representing the URL of the topic which is updated
	 * @return
	 */
	public ResponseEntity<String> distribute(String callbackUrl, String content, MediaType contentType, String topicUrl);
}
