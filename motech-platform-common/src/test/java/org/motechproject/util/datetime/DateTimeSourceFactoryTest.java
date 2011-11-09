package org.motechproject.util.datetime;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DateTimeSourceFactoryTest {
    @Test
    public void createWithoutTestMode() throws Exception {
        DateTimeSource dateTimeSource = DateTimeSourceFactory.create();
        assertEquals(DefaultDateTimeSource.class, dateTimeSource.getClass());
        assertEquals("India Standard Time", dateTimeSource.timeZone().toTimeZone().getDisplayName());
    }

    @Test
    public void defaultsToNonTestMode() {
        DateTimeSource dateTimeSource = DateTimeSourceFactory.create("nonexistentfile");
        assertEquals(DefaultDateTimeSource.class, dateTimeSource.getClass());
        assertEquals("India Standard Time", dateTimeSource.timeZone().toTimeZone().getDisplayName());
    }

    @Test
    public void testMode() {
        DateTimeSource dateTimeSource = DateTimeSourceFactory.create("/dateForTestMode.properties");
        assertEquals(ExternalDateTimeSource.class, dateTimeSource.getClass());
        assertEquals("India Standard Time", dateTimeSource.timeZone().toTimeZone().getDisplayName());
    }
}
