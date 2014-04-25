package org.motechproject.admin.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception representing a situation when a given bundle was not found. Will cause a 404 on the UI.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class BundleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -1815262188243219664L;

    public BundleNotFoundException(String message) {
        super(message);
    }
}
