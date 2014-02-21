package org.motechproject.mds.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class TypeHelperTest {

    @Test
    public void shouldParseStrings() {
        final DateTime dt = DateUtil.now();
        final DateTime dt2 = new DateTime(2009, 6, 7, 12, 11, 0, 0, DateTimeZone.forOffsetHours(1));

        assertEquals(3, TypeHelper.parse(3, Integer.class));
        assertEquals("test", TypeHelper.parse("test", String.class));
        assertEquals(new Time(16, 4), TypeHelper.parse("16:04", Time.class));
        assertEquals(dt, TypeHelper.parse(dt.toString(), DateTime.class));
        assertEquals(dt.toDate(), TypeHelper.parse(dt.toString(), Date.class));
        assertEquals(DateUtil.setTimeZoneUTC(dt2),
                DateUtil.setTimeZoneUTC((DateTime) TypeHelper.parse("2009-06-07 12:11 +01:00", DateTime.class)));
        assertEquals(dt2.toDate(), TypeHelper.parse("2009-06-07 12:11 +01:00", Date.class));
        assertEquals(asList("one", "2", "three"), TypeHelper.parse("one\n2\nthree", List.class));
        assertEquals(true, TypeHelper.parse("true", Boolean.class));
    }

    @Test
    public void shouldReturnCorrectInstances() {
        final DateTime dt = DateUtil.now();
        assertEquals(new Time(10, 10), TypeHelper.parse(new Time(10, 10), Time.class));
        assertEquals(dt, TypeHelper.parse(dt, DateTime.class));
        assertEquals(dt.toDate(), TypeHelper.parse(dt.toDate(), Date.class));
        assertEquals(11, TypeHelper.parse(11, Integer.class));
        assertEquals(asList(1, 2), asList(1, 2));
    }

    @Test
    public void shouldParseIntToBool() {
        assertEquals(true, TypeHelper.parse(1, Boolean.class));
        assertEquals(true, TypeHelper.parse(200, Boolean.class));
        assertEquals(false, TypeHelper.parse(0, Boolean.class));
        assertEquals(false, TypeHelper.parse(-1, Boolean.class));
        assertEquals(null, TypeHelper.parse(null, Boolean.class));
    }
}
