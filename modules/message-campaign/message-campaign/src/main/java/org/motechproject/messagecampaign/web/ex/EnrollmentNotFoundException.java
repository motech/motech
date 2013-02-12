package org.motechproject.messagecampaign.web.ex;


public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException() {
    }

    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
