package org.motechproject.server.event;

import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.metrics.impl.MultipleMetricsAgentImpl;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventListenerRegistryTest {
    private EventListenerRegistry registry;

    @Before
    public void setUp() {
        registry = new EventListenerRegistry(new MultipleMetricsAgentImpl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEventListenerRegistration() {
        EventListener sel = null;
        registry.registerListener(sel, "org.motechproject.server.someevent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullEventTypeRegistration() {
        EventListener sel = new SampleEventListener();

        registry.registerListener(sel, (String) null);
    }

    @Test
    public void testEmptyEventListRegistration() throws NoSuchFieldException {
        List<String> subjects = new ArrayList<String>();
        EventListener sel = new SampleEventListener();

        EventListenerTree mockTree = mock(EventListenerTree.class);
        PrivateAccessor.setField(registry, "listenerTree", mockTree);

        registry.registerListener(sel, subjects);

        verify(mockTree, times(0)).addListener(Matchers.<EventListener>anyObject(), anyString());
    }

    @Test
    public void testRegisterSingleListener() {
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, "org.motechproject.server.someevent");

        Set<EventListener> listeners = registry.getListeners("org.motechproject.server.someevent");
        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), sel);
    }

    @Test
    public void testHasListener_Yes() {
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, "org.motechproject.server.someevent");

        assertTrue(registry.hasListener("org.motechproject.server.someevent"));
    }

    @Test
    public void testHasListener_YesWildcard() {
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, "org.motechproject.server.*");

        assertTrue(registry.hasListener("org.motechproject.server.someevent"));
    }

    @Test
    public void testHasListener_NoEmpty() {
        EventListener sel = new SampleEventListener();

        assertFalse(registry.hasListener("org.motechproject.server.someevent"));
    }

    @Test
    public void testHasListener_No() {
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, "org.motechproject.server.someevent");

        assertFalse(registry.hasListener("org.motechproject.client.otherevent"));
    }

    @Test
    public void testRegisterMultipleListener() {

        EventListener sel = new SampleEventListener();
        EventListener sel2 = new FooEventListener();

        registry.registerListener(sel, "org.motechproject.server.someevent");
        registry.registerListener(sel2, "org.motechproject.server.someevent");

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent");
        assertNotNull(el);
        assertTrue(el.size() == 2);
        assertTrue(el.contains(sel));
        assertTrue(el.contains(sel2));
    }

    @Test
    public void testRegisterForMultipleEvents() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");
        et.add("org.motechproject.server.someotherevent");

        EventListener sel = new SampleEventListener();

        registry.registerListener(sel, et);

        Set<EventListener> el = registry.getListeners(et.get(0));
        assertNotNull(el);
        assertTrue(el.size() == 1);
        assertTrue(el.contains(sel));

        el = registry.getListeners(et.get(1));
        assertNotNull(el);
        assertTrue(el.size() == 1);
        assertTrue(el.contains(sel));
    }

    @Test
    public void testRegisterTwice() {

        EventListener sel = new SampleEventListener();

        registry.registerListener(sel, "org.motechproject.server.someevent");
        registry.registerListener(sel, "org.motechproject.server.someevent");

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent");
        assertNotNull(el);
        assertTrue(el.size() == 1);
        assertTrue(el.contains(sel));
    }


    @Test
    public void testRegisterForSameEventTwice() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");
        et.add("org.motechproject.server.someevent");

        EventListener sel = new SampleEventListener();

        registry.registerListener(sel, et);

        Set<EventListener> el = registry.getListeners(et.get(0));
        assertNotNull(el);
        assertTrue(el.size() == 1);
        assertTrue(el.contains(sel));

        el = registry.getListeners(et.get(1));
        assertNotNull(el);
        assertTrue(el.size() == 1);
        assertTrue(el.contains(sel));
    }

    @Test
    public void testGetListeners() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, et);

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent");
        assertNotNull(el);
        assertEquals(1, el.size());
        assertEquals(el.iterator().next(), sel);
    }

    @Test
    public void testGetEmptyListenerList() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");

        assertEquals(0, registry.getListeners(et.get(0)).size());
    }

    @Test
    public void testAddThenRemoveListener() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, et);

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(1, el.size());

        registry.clearListenersForBean("TestEventListener");

        el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(0, el.size());
    }

    @Test
    public void testAddingWildCardListenerThenRemoving() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.*");
        EventListener sel = new SampleEventListener();
        registry.registerListener(sel, et);

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent.test");

        assertEquals(1, el.size());

        registry.clearListenersForBean("TestEventListener");

        el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(0, el.size());
    }

    @Test
    public void testRemovingListenerPreservesOtherListeners() {
        List<String> et = new ArrayList<String>();
        et.add("org.motechproject.server.someevent");
        EventListener sel = new SampleEventListener();
        EventListener se2 = new OtherSampleEventListener();

        registry.registerListener(sel, et);
        registry.registerListener(se2, et);

        Set<EventListener> el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(2, el.size());

        registry.clearListenersForBean("TestEventListener");

        el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(1, el.size());

        registry.clearListenersForBean("TestEventListener2");

        el = registry.getListeners("org.motechproject.server.someevent");

        assertEquals(0, el.size());
    }

    class FooEventListener implements EventListener {

        @Override
        public void handle(MotechEvent event) {
        }

        @Override
        public String getIdentifier() {
            return "FooEventListener";
        }
    }
}

