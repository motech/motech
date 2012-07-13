package org.motechproject.commcare.exception;

public class MalformedCaseXmlException extends RuntimeException {

    public MalformedCaseXmlException(String message) {
        super(message);
    }

    public MalformedCaseXmlException(Exception ex, String message) {
        super(message, ex);
    }
}
