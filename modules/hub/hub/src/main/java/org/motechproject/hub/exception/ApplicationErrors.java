package org.motechproject.hub.exception;

import org.springframework.http.HttpStatus;


public enum ApplicationErrors implements HubErrors {

	//TODO: standardize the codes
	BAD_REQUEST (1001, "One or more input parameter(s) may be wrong", HttpStatus.BAD_REQUEST),
	SUBSCRIBER_NOT_FOUND (1002, "Subscription intent verification failed", HttpStatus.BAD_REQUEST),
	TOPIC_NOT_FOUND (1003, "Topic not found", HttpStatus.BAD_REQUEST),
	SUBSCRIPTION_NOT_FOUND (1004, "Subscription not found", HttpStatus.BAD_REQUEST),
	TOPIC_ALREADY_SUBSCRIBED (1005, "Subscriber already subscribed to the requested topic", HttpStatus.BAD_REQUEST);

	private final int code;
	private String message;
	private HttpStatus httpStatus;

	private ApplicationErrors() {
		code = 1000;
	}
	private ApplicationErrors(int code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

}
