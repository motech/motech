package org.motechproject.admin.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BundleNotFoundException extends RuntimeException {

    public BundleNotFoundException(String message) {
        super(message);
    }
}
