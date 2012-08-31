package org.motechproject.mobileforms.api.callbacks;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FormGroupPublisher {

    private final Logger log = LoggerFactory.getLogger(FormGroupPublisher.class);

    public static final String FORM_BEAN_GROUP = "formBeanGroup";
    public static final String FORM_VALID_FROMS = "handle.valid.xforms.group";

    @Autowired
    private EventRelay eventRelay;

    public FormGroupPublisher() {
    }

    public void publish(FormBeanGroup formBeanGroup) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(FORM_BEAN_GROUP, formBeanGroup);
            MotechEvent motechEvent = new MotechEvent(FORM_VALID_FROMS, params);
            eventRelay.sendEventMessage(motechEvent);
        } catch (Exception e) {
            formBeanGroup.markAllFormAsFailed("Server exception, contact your administrator");
            log.error("Encountered exception while validating form group, " + formBeanGroup.toString(), e);
        }
    }
}
