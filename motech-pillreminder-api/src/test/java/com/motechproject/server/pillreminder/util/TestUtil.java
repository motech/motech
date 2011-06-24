package com.motechproject.server.pillreminder.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class TestUtil {

    public static Date newDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    @Test
    public void shouldTest(){
       assertTrue(true);
        //TBD to add filters in POM.
    }
}
