package com.motechproject.server.pillreminder.util;

import java.util.Calendar;
import java.util.Date;

public class TestUtil {

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }
}
