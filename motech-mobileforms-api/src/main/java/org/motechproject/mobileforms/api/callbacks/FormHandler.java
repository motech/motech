package org.motechproject.mobileforms.api.callbacks;

import org.motechproject.mobileforms.api.domain.FormData;

public interface FormHandler {
    void handle(FormData formData);
}
