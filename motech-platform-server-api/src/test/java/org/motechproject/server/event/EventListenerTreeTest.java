package org.motechproject.server.event;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventListenerTreeTest {
    private EventListenerTree tree;
    private EventListener el = new SampleEventListener();

    private String EVENT1 = "org.motechproject.server.some-event";
    private String EVENT2 = "org.motechproject.server.some-other-event";
    private String WILDCARD_EVENT = "org.motechproject.server.*";

    @Before
    public void setUp() {
        tree = new EventListenerTree();
    }

    @Test
    public void testGetSubject_NoListeners() {
        assertEquals("", tree.getSubject());
    }

    @Test
    public void testAddListener_SingleListener() {
        tree.addListener(el, EVENT1);

        assertTrue(tree.hasListener(EVENT1));
        assertFalse(tree.hasListener(EVENT2));
    }

    @Test
    public void testAddListener_DoubleListener() {
        tree.addListener(el, EVENT1);

        assertTrue(tree.hasListener(EVENT1));
        Set<EventListener> listeners = tree.getListeners(EVENT1);

        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), el);
    }

    @Test
    public void testAddListener_WildcardListener() {
        tree.addListener(el, WILDCARD_EVENT);

        assertTrue(tree.hasListener(EVENT1));
        assertTrue(tree.hasListener(EVENT2));
        Set<EventListener> listeners = tree.getListeners(EVENT1);

        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), el);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectWildcardInMiddle() {
        tree.addListener(el, "org.motechproject.*.event");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectEmptyPath() {
        tree.addListener(el, "org.motechproject..event");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectWildcard() {
        tree.addListener(el, "org.motechproject.event*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_Nullsubject() {
        tree.addListener(el, null);
    }

    @Test
    public void getListenerCount_Simple() {
        tree.addListener(el, EVENT1);

        assertEquals(1, tree.getListenerCount(EVENT1));
        assertEquals(0, tree.getListenerCount(EVENT2));
    }

    @Test
    public void getListenerCount_Wildcard() {
        tree.addListener(el, WILDCARD_EVENT);

        assertEquals(1, tree.getListenerCount(EVENT1));
        assertEquals(1, tree.getListenerCount(EVENT2));
    }

    @Test
    public void getListenerCount_Multiple() {
        tree.addListener(el, WILDCARD_EVENT);
        tree.addListener(new FooEventListener(), EVENT1);

        assertEquals(2, tree.getListenerCount(EVENT1));
        assertEquals(1, tree.getListenerCount(EVENT2));
    }

    @Test
    public void testRemoveAllListeners() {
        tree.addListener(new FooEventListener(), EVENT1);
        tree.addListener(new FooEventListener(), EVENT2);

        assertEquals(1, tree.getListenerCount(EVENT1));
        assertEquals(1, tree.getListenerCount(EVENT2));

        tree.removeAllListeners("FooEventListener");

        assertEquals(0, tree.getListenerCount(EVENT1));
        assertEquals(0, tree.getListenerCount(EVENT2));
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

