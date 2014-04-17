package org.motechproject.batch.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String reason;

	private BatchException batchException;

	public RestException(BatchException exception, List<String> errors) {
		this(exception, errors.toString());
	}

	public RestException(BatchException batchException, String reason) {
		super("HttpStatus:" + batchException.getError().getHttpStatus()
				+ " reason:" + reason);
		this.reason = reason;
		this.batchException = batchException;
	}
	
	public RestException(Exception batchException, String reason) {
		super("HttpStatus:" + 500
				+ " reason:" + reason);
		this.reason = reason;
		//this.batchException = batchException;
	}

	public HttpStatus getHttpStatus() {
		return getBatchException().getError().getHttpStatus();
	}

	public String getReason() {
		return reason;
	}

	public BatchException getBatchException() {
		return batchException;
	}
}
