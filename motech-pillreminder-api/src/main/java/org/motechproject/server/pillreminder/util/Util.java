package org.motechproject.server.pillreminder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static Date newDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    public static boolean areDatesSame(Date date1, Date date2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        return dateFormat.format(date1).equals(dateFormat.format(date2));
    }

    public static Date getEndDateAfter(Date startDate, int numOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.MONTH, numOfMonth);
        return cal.getTime();
    }
}
