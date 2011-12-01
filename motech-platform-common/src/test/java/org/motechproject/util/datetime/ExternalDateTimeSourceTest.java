package org.motechproject.util.datetime;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalDateTimeSourceTest {
    private ExternalDateTimeSource dateTimeSource;
    private DateTimeConfiguration configuration;

    @Before
    public void setUp() {
        configuration = mock(DateTimeConfiguration.class);
        dateTimeSource = new ExternalDateTimeSource(configuration);
    }

    @Test
    public void useToday() {
        when(configuration.currentValueFor(DateTimeConfiguration.TODAY_PROPERTY_NAME)).thenReturn("2011-10-17");
        assertEquals(DateUtil.newDate(2011, 10, 17), dateTimeSource.today());
        DateTime now = dateTimeSource.now();
        assertEquals(17, now.getDayOfMonth());
        assertEquals(10, now.getMonthOfYear());
        assertEquals(2011, now.getYear());
    }

    @Test
    public void todayShouldReturnSystemDateWhenPropertyValueIsNotSpecified() {
        when(configuration.currentValueFor(DateTimeConfiguration.TODAY_PROPERTY_NAME)).thenReturn(null);
        assertNotNull(dateTimeSource.today());
    }
}
