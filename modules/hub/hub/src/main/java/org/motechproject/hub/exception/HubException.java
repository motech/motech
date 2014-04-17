package org.motechproject.hub.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class HubException extends Exception {

	private HubErrors hubErrors;
	private String reason;

	public HubException(HubErrors hubErrors) {
		super(hubErrors.getMessage());
		this.hubErrors = hubErrors;
	}

	public HubException(HubErrors hubErrors, String reason) {
		super(hubErrors.getMessage());
		this.hubErrors = hubErrors;
		this.reason = reason;
	}

	public HubException(HubErrors hubErrors, Throwable throwable) {
		super(hubErrors.getMessage(), throwable);
		this.hubErrors = hubErrors;
	}

	public HubException(HubErrors hubErrors, Throwable throwable,
			String reason) {
		super(hubErrors.getMessage(), throwable);
		this.hubErrors = hubErrors;
		this.reason = reason;
	}

	public int getErrorCode() {
		return hubErrors.getCode();
	}

	public String getErrorMessage() {
		if (reason == null || reason.length() < 1) {
			return this.getMessage();
		} else {
			return this.getMessage() + ". Reason: " + reason;
		}
	}

	public HubErrors getError() {
		return hubErrors;
	}

	public String getErrorMessageDetails() {

		return getStackTraceString();
	}

	private String getStackTraceString() {
		String stackTrace = null;

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter, true);
		this.printStackTrace(printWriter);
		printWriter.flush();
		stringWriter.flush();
		stackTrace = stringWriter.toString();

		return stackTrace;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}
