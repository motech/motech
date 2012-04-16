package org.motechproject.server.pillreminder.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosageCapturedTest extends BaseUnitTest{

    @Test
    public void isDosageCapturedForToday() {
        DateTime now = new DateTime(2012, 4, 16, 8, 30);
        Dosage dosage = new Dosage(new Time(9, 5), new HashSet<Medicine>());

        mockCurrentDate(now);

        assertFalse(dosage.isTodaysDosageResponseCaptured());

        dosage.setResponseLastCapturedDate(DateUtil.today());
        assertTrue(dosage.isTodaysDosageResponseCaptured());
    }

    @Test
    public void isDosageCapturedForTodayWhenItIsYesterdaysDoseAndPillReminderCallsSpillOverToToday() {
        DateTime now = new DateTime(2012, 4, 16, 0, 30);
        Dosage dosage = new Dosage(new Time(23, 5), new HashSet<Medicine>());

        mockCurrentDate(now);

        assertFalse(dosage.isTodaysDosageResponseCaptured());

        dosage.setResponseLastCapturedDate(DateUtil.today().minusDays(1));
        assertTrue(dosage.isTodaysDosageResponseCaptured());
    }

    @Test
    public void isDosageCapturedForTodayIsFalse_WhenCapturedForYesterday_AndTimeIsAfterPillTime() {
        DateTime now = new DateTime(2012, 4, 16, 9, 30);
        Dosage dosage = new Dosage(new Time(9, 5), new HashSet<Medicine>());

        mockCurrentDate(now);

        dosage.setResponseLastCapturedDate(DateUtil.today().minusDays(1));
        assertFalse(dosage.isTodaysDosageResponseCaptured());
    }

    @Test
    public void isDosageCapturedForTodayIsTrue_WhenCapturedForYesterday_AndTimeIsBeforePillTime() {
        DateTime now = new DateTime(2012, 4, 16, 9, 4);
        Dosage dosage = new Dosage(new Time(9, 5), new HashSet<Medicine>());

        mockCurrentDate(now);

        dosage.setResponseLastCapturedDate(DateUtil.today().minusDays(1));
        assertTrue(dosage.isTodaysDosageResponseCaptured());
    }
}