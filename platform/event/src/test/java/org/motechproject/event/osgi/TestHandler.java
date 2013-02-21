package org.motechproject.event.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.event.listener.annotations.MotechListenerType;
import org.motechproject.event.listener.annotations.MotechParam;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TestHandler {


    public final static List<String> EVENTS_HANDLED = Collections.synchronizedList(new ArrayList<String>());
    public static final String TEST_SUBJECT = "test-subject";

    @MotechListener(subjects = {TEST_SUBJECT})
    public void handle(MotechEvent event) {
        add(event);
    }


    @MotechListener(subjects = {"sub_a", "sub_b"})
    public void handleX(MotechEvent event) {
        add(event);
    }

    @MotechListener(subjects = {"sub_a", "sub_c"})
    public void handleY(MotechEvent event) {
    }

    @MotechListener(subjects = {"params"}, type = MotechListenerType.ORDERED_PARAMETERS)
    public void handleParams(Integer a, Integer b, String s) {
    }

    @MotechListener(subjects = {"exception"}, type = MotechListenerType.ORDERED_PARAMETERS)
    public void orderedParams(Integer a, Integer b, String s) {
        Assert.notNull(s, "s must not be null");
    }

    @MotechListener(subjects = {"named"}, type = MotechListenerType.NAMED_PARAMETERS)
    public void namedParams(@MotechParam("id") String id, @MotechParam("key") String key) {
    }

    private void add(MotechEvent event) {
        EVENTS_HANDLED.add(event.getSubject());
    }

}
