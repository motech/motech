package org.motechproject.mobileforms.api.osgi.it;

import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.osgi.test.AbstractTaskBundleIT;

import java.io.IOException;
import java.util.List;

import static org.motechproject.mobileforms.api.callbacks.FormGroupPublisher.FORM_BEAN_GROUP;
import static org.motechproject.mobileforms.api.callbacks.FormGroupPublisher.FORM_ERROR;
import static org.motechproject.mobileforms.api.callbacks.FormGroupPublisher.FORM_VALID_FROMS;

public class MobileFormsTaskBundleIT extends AbstractTaskBundleIT {

    public void testValidFormTriggerExists() throws IOException {
        assertTrigger(FORM_VALID_FROMS);
    }

    public void testFormErrorTriggerExists() throws IOException {
        assertTrigger(FORM_ERROR);
    }

    private void assertTrigger(String subject) throws IOException {
        Channel channel = findChannel("mobileforms");
        assertNotNull(channel);
        List<TriggerEvent> triggerTaskEvents = channel.getTriggerTaskEvents();
        TriggerEvent trigger = findTriggerEventBySubject(triggerTaskEvents, subject);
        assertNotNull(trigger);
        assertTrue(hasEventParameterKey(FORM_BEAN_GROUP, trigger.getEventParameters()));
    }
}
