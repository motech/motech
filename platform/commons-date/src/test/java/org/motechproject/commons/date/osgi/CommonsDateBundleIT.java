package org.motechproject.commons.date.osgi;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.commons.date.util.datetime.DateTimeSource;
import org.motechproject.commons.date.valueobjects.WallTime;
import org.motechproject.testing.osgi.BasePaxIT;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class CommonsDateBundleIT extends BasePaxIT {

    @Test
    public void testCommonsDate() {
        assertEquals(3, new WallTime("3 Day").inDays());
        assertEquals(7, new WallTime("1 Week").inDays());

        DateTimeSourceUtil.setSourceInstance(new DateTimeSource() {
            @Override
            public DateTimeZone timeZone() {
                return DateTimeZone.UTC;
            }

            @Override
            public DateTime now() {
                return new DateTime(0);
            }

            @Override
            public LocalDate today() {
                return new LocalDate(0);
            }
        });

        assertEquals(DateUtil.today(), new LocalDate(0));
    }
}
