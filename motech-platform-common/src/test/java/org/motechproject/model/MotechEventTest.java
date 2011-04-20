package org.motechproject.model;

import org.junit.Test;

/**
 * MotechEvent Tester.
 */
public class MotechEventTest {
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullSubject() throws Exception {
        MotechEvent event = new MotechEvent(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_WildcardSubject() throws Exception {
        MotechEvent event = new MotechEvent("org.motechproject.*", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_EmptyPathSubject() throws Exception {
        MotechEvent event = new MotechEvent("org.motechproject..event", null);
    }
}
