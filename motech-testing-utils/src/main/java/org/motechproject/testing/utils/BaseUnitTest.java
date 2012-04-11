package org.motechproject.testing.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
    static final DateTimeSource dateTimeSource = new DefaultDateTimeSource();

    protected void mockCurrentDate(DateTime currentDateTime) {
        DateTimeSourceUtil.SourceInstance = new MockDateTimeSource(currentDateTime);
    }

    protected void mockCurrentDate(LocalDate currentDate) {
        DateTimeSourceUtil.SourceInstance = new MockDateTimeSource(currentDate);
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
        DateTimeSourceUtil.SourceInstance = dateTimeSource;
    }

    @After
    public void tearDown() {
        resetDateTimeSource();
    }

    class MockDateTimeSource implements DateTimeSource {
        private DateTime dateTime;

        MockDateTimeSource(LocalDate localDate) {
            this(localDate.toDateTime(LocalTime.MIDNIGHT));
        }

        MockDateTimeSource(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public DateTimeZone timeZone() {
            return dateTime.getZone();
        }

        @Override
        public DateTime now() {
            return dateTime;
        }

        @Override
        public LocalDate today() {
            return dateTime.toLocalDate();
        }
    }
}
