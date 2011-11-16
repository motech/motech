package org.motechproject.server.messagecampaign;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.datetime.DateTimeSource;

public class TestUtils {

    public static void mockCurrentDate(final DateTime currentDate) {
        DateTimeSourceUtil.SourceInstance = new DateTimeSource() {

            @Override
            public DateTimeZone timeZone() {
                return currentDate.getZone();
            }

            @Override
            public DateTime now() {
                return currentDate;
            }

            @Override
            public LocalDate today() {
                return currentDate.toLocalDate();
            }
        };
    }

    public static DateTime date(int year, int monthOfYear, int dayOfMonth) {
        return new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }
}
