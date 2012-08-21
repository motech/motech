package org.motechproject.event;

import java.util.UUID;

public class MotechEventTransformer {

    public MotechEvent transform(MotechEvent motechEvent) {
        if (motechEvent.getId() == null) {
            motechEvent.setId(UUID.randomUUID());
        }
        return motechEvent;
    }
}
