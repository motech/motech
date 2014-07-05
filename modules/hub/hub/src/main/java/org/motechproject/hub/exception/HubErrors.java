package org.motechproject.hub.exception;

import org.springframework.http.HttpStatus;

public interface HubErrors {

    String getMessage();

    int getCode();

    HttpStatus getHttpStatus();
}
