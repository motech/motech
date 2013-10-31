package org.motechproject.mds.exception;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MDSValidationErrors {

    private List<String> errors = new ArrayList<>();

    public void add(String message) {
        errors.add(message);
    }

    public String allErrorKeys() {
        return StringUtils.join(errors, ",");
    }
}
