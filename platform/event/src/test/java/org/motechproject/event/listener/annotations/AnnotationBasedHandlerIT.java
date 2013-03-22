package org.motechproject.event.listener.annotations;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.ServerEventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class AnnotationBasedHandlerIT {

    static boolean test = false;


    @Autowired
    ServerEventRelay eventRelay;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;

    private void send(String dest, Object... objects) {
        Map<String, Object> params = new HashMap<String, Object>();
        int i = 0;
        for (Object obj : objects) {
            params.put(Integer.toString(i++), obj);
        }
        MotechEvent event = new MotechEvent(dest, params);
        eventRelay.relayEvent(event);
    }


    public static void clear() {
        test=false;
    }

    @Test
    public void testRegistry() {
        assertEquals(2, eventListenerRegistry.getListenerCount("sub_a"));
        assertEquals(1,eventListenerRegistry.getListenerCount("sub_b"));
        assertEquals(1,eventListenerRegistry.getListenerCount("sub_c"));
    }

    @Test
    public void testRelay() {
        MotechEvent e = new MotechEvent("sub_b", null);
        clear();
        eventRelay.relayEvent(e);
        assertTrue(test);

        e = new MotechEvent("sub_c", null);
        clear();
        eventRelay.relayEvent(e);
        assertTrue(test);
    }

    @Test
    public void testOrderedParams() {
        clear();
        send("params",23,44,null);
        assertTrue(test);
    }

    @Test
    public void testNamedParamsHappy() {
        clear();
        MotechEvent event = new MotechEvent("named");
        event.getParameters().put("id", "id0012");
        event.getParameters().put("key", "2354");
        eventRelay.relayEvent(event);
        assertTrue(test);
    }
}
