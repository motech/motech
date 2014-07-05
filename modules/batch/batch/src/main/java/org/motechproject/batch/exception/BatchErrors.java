package org.motechproject.batch.exception;

import org.springframework.http.HttpStatus;

public interface BatchErrors {

    String getMessage();

    int getCode();

    HttpStatus getHttpStatus();
}
