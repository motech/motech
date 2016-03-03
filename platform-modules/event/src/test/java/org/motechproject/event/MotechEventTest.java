package org.motechproject.event;

import org.junit.Test;

/**
 * MotechEvent Tester.
 */
public class MotechEventTest {
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullSubject() throws Exception {
        new MotechEvent(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_WildcardSubject() throws Exception {
        new MotechEvent("org.motechproject.*", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_EmptyPathSubject() throws Exception {
        new MotechEvent("org.motechproject..event", null);
    }
}
