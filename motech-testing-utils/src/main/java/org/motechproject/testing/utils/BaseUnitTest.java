package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.After;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.datetime.DateTimeSource;
import org.motechproject.util.datetime.DefaultDateTimeSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseUnitTest {
    static final DateTimeSource DATE_TIME_SOURCE = new DefaultDateTimeSource();

    protected void mockCurrentDate(DateTime currentDateTime) {
        DateTimeSourceUtil.setSourceInstance(new FakeDateTimeSource(currentDateTime));
    }

    protected void mockCurrentDate(LocalDate currentDate) {
        DateTimeSourceUtil.setSourceInstance(new FakeDateTimeSource(currentDate));
    }

    protected DateTime date(int year, int monthOfYear, int dayOfMonth) {
        return new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }

    protected Date date(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected DateTime dateTime(int year, int monthOfYear, int dayOfMonth, LocalTime localTime) {
        return new DateTime(year, monthOfYear, dayOfMonth, localTime.getHourOfDay(), localTime.getMinuteOfHour());
    }

    protected DateTime dateTime(LocalDate localDate, LocalTime localTime) {
        return new DateTime(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth(), localTime.getHourOfDay(), localTime.getMinuteOfHour());
    }

    protected void resetDateTimeSource() {
        DateTimeSourceUtil.setSourceInstance(DATE_TIME_SOURCE);
    }

    @After
    public void tearDown() {
        resetDateTimeSource();
    }

}
