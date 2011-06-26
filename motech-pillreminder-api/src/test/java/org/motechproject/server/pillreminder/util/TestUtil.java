package org.motechproject.server.pillreminder.util;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TestUtil {

    public static Date newDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    public static boolean areDatesSame(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G HH:mm:ss z");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    @Test
    public void shouldTest() {
        assertTrue(true);
    }
}
