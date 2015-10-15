package org.motechproject.mds.display;


import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.motechproject.mds.display.UIRepresentationUtil.uiRepresentationString;

public class UIRepresentationUtilTest {

    @Test
    public void shouldReturnTestString() {
        Object instance1 =  new Sample();
        Object instance2 = new Sample2();
        assertEquals("testString", uiRepresentationString(instance1));
        assertEquals("testString", uiRepresentationString(instance2));
    }

    @Test
    public void shouldReturnNullForInstanceWithInvalidRepresentation() {
        Object instance = new Sample1();
        assertNull(uiRepresentationString(instance));
    }
}
