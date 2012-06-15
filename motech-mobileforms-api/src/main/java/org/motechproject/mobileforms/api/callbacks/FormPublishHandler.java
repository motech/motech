package org.motechproject.mobileforms.api.callbacks;

import org.motechproject.scheduler.domain.MotechEvent;

public interface FormPublishHandler {
    void handleFormEvent(MotechEvent event);
}
