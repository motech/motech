package org.motechproject.server.messagecampaign.domain;

public class CampainNotActiveException extends RuntimeException {

    public CampainNotActiveException(String message) {
        super(message);
    }
}
