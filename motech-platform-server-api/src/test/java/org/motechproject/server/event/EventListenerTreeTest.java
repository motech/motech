package org.motechproject.server.event;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduler.domain.MotechEvent;

import java.util.Set;

import static org.junit.Assert.*;

public class EventListenerTreeTest {
    private EventListenerTree tree;
    private EventListener listener = new SampleEventListener();

    private String SUBJECT_0 = "org.motechproject.server";
    private String SUBJECT_1 = "org.motechproject.server.some-event";
    private String SUBJECT_2 = "org.motechproject.server.some-other-event";
    private String WILDCARD_SUBJECT = "org.motechproject.server.*";

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
        tree.addListener(listener, SUBJECT_1);

        assertTrue(tree.hasListener(SUBJECT_1));
        assertFalse(tree.hasListener(SUBJECT_2));
    }

    @Test
    public void testAddListener_DoubleListener() {
        tree.addListener(listener, SUBJECT_1);

        assertTrue(tree.hasListener(SUBJECT_1));
        Set<EventListener> listeners = tree.getListeners(SUBJECT_1);

        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), listener);
    }

    @Test
    public void testAddListener_WildcardListener() {
        tree.addListener(listener, WILDCARD_SUBJECT);
        assertTrue(tree.hasListener(SUBJECT_1));
        assertTrue(tree.hasListener(SUBJECT_2));

        Set<EventListener> listeners = tree.getListeners(SUBJECT_1);
        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), listener);
    }

    @Test
    public void testAddListener_WildcardListener2() {
        tree.addListener(listener, WILDCARD_SUBJECT);
        assertEquals(1, tree.getListeners(SUBJECT_0).size());

        Set<EventListener> listeners = tree.getListeners(SUBJECT_0);
        assertNotNull(listeners);
        assertTrue(listeners.size() == 1);
        assertEquals(listeners.iterator().next(), listener);
    }

    @Test
    public void testAddListener_UniversalListener() {
        EventListener listener1 = new SampleEventListener();
        EventListener listener2 = new SampleEventListener();
        tree.addListener(listener1, "*");
        tree.addListener(listener2, "org.test");

        Set<EventListener> listeners = tree.getListeners("*");
        assertTrue(listeners.size() == 1);
        assertTrue(listeners.contains(listener1));

        listeners = tree.getListeners("org.test");
        assertTrue(listeners.size() == 2);
        assertTrue(listeners.contains(listener1));
        assertTrue(listeners.contains(listener2));

        listeners = tree.getListeners("com.pqr.xyz");
        assertTrue(listeners.size() == 1);
        assertTrue(listeners.contains(listener1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectWildcardInMiddle() {
        tree.addListener(listener, "org.motechproject.*.event");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectEmptyPath() {
        tree.addListener(listener, "org.motechproject..event");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_InvalidSubjectWildcard() {
        tree.addListener(listener, "org.motechproject.event*");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_Nullsubject() {
        tree.addListener(listener, null);
    }

    @Test
    public void getListenerCount_Simple() {
        tree.addListener(listener, SUBJECT_1);

        assertEquals(1, tree.getListenerCount(SUBJECT_1));
        assertEquals(0, tree.getListenerCount(SUBJECT_2));
    }

    @Test
    public void getListenerCount_Wildcard() {
        tree.addListener(listener, WILDCARD_SUBJECT);

        assertEquals(1, tree.getListenerCount(SUBJECT_1));
        assertEquals(1, tree.getListenerCount(SUBJECT_2));
    }

    @Test
    public void getListenerCount_Multiple() {
        tree.addListener(listener, WILDCARD_SUBJECT);
        tree.addListener(new FooEventListener(), SUBJECT_1);

        assertEquals(2, tree.getListenerCount(SUBJECT_1));
        assertEquals(1, tree.getListenerCount(SUBJECT_2));
    }

    @Test
    public void testRemoveAllListeners() {
        tree.addListener(new FooEventListener(), SUBJECT_1);
        tree.addListener(new FooEventListener(), SUBJECT_2);

        assertEquals(1, tree.getListenerCount(SUBJECT_1));
        assertEquals(1, tree.getListenerCount(SUBJECT_2));

        tree.removeAllListeners("FooEventListener");

        assertEquals(0, tree.getListenerCount(SUBJECT_1));
        assertEquals(0, tree.getListenerCount(SUBJECT_2));
    }

    @Test
    public void testRemoveAllNonWildcardListeners() {
        tree.addListener(new FooEventListener(), SUBJECT_1);
        tree.addListener(new FooEventListener(), SUBJECT_2);
        tree.addListener(new BarEventListener(), WILDCARD_SUBJECT);

        assertEquals(2, tree.getListenerCount(SUBJECT_1));
        assertEquals(2, tree.getListenerCount(SUBJECT_2));

        tree.removeAllListeners("FooEventListener");

        assertEquals(1, tree.getListenerCount(SUBJECT_1));
        assertEquals(1, tree.getListenerCount(SUBJECT_2));
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

    class BarEventListener implements EventListener {

        @Override
        public void handle(MotechEvent event) {
        }

        @Override
        public String getIdentifier() {
            return "BarEventListener";
        }
    }
}

