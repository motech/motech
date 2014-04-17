package org.motechproject.hub.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String reason;

	private HubException hubException;

	public RestException(HubException exception, List<String> errors) {
		this(exception, errors.toString());
	}

	public RestException(HubException hubException, String reason) {
		super("HttpStatus:" + hubException.getError().getHttpStatus()
				+ " reason:" + reason);
		this.reason = reason;
		this.hubException = hubException;
	}

	public HttpStatus getHttpStatus() {
		return getHubException().getError().getHttpStatus();
	}

	public String getReason() {
		return reason;
	}

	public HubException getHubException() {
		return hubException;
	}
}
