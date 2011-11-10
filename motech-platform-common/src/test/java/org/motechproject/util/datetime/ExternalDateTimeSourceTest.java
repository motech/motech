package org.motechproject.util.datetime;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalDateTimeSourceTest {
    @Test
    public void useToday() {
        DateTimeConfiguration configuration = mock(DateTimeConfiguration.class);
        when(configuration.currentValueFor(DateTimeConfiguration.TODAY_PROPERTY_NAME)).thenReturn("2011-10-17");
        ExternalDateTimeSource dateTimeSource = new ExternalDateTimeSource(configuration);
        assertEquals(DateUtil.newDate(2011, 10, 17), dateTimeSource.today());
        DateTime now = dateTimeSource.now();
        assertEquals(17, now.getDayOfMonth());
        assertEquals(10, now.getMonthOfYear());
        assertEquals(2011, now.getYear());
    }
}
