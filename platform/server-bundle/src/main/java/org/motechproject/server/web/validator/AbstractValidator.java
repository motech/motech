package org.motechproject.server.web.validator;

import org.motechproject.server.web.form.StartupForm;

import java.util.List;

/**
 * Basic interface which startup settings validators implement
 */
public interface AbstractValidator {
    void validate(StartupForm target, List<String> errors);
}
