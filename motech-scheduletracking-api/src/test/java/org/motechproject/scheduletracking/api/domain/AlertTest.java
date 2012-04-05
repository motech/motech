package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.days;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.hours;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.powermock.api.mockito.PowerMockito.spy;

@PrepareForTest(DateUtil.class)
@RunWith(PowerMockRunner.class)
public class AlertTest {

    private DateTime now;

    @Before
    public void setUp() {
        now = new DateTime(2012, 2, 20, 8, 15, 0, 0);
        spy(DateUtil.class);
        given(DateUtil.now()).willReturn(now);
        given(DateUtil.today()).willReturn(now.toLocalDate());
    }

    @Test
    public void shouldReturnElapsedAlertCountWithZeroOffset() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        int alertCount = alert.getElapsedAlertCount(newDateTime(2012, 2, 18, 0, 0, 0), new Time(8, 10));
        assertEquals(3, alertCount);
    }

    @Test
    public void shouldReturnElapsedAlertCountWithNonZeroOffset() {
        Alert alert = new Alert(days(1), days(1), 10, 0);
        int alertCount = alert.getElapsedAlertCount(newDateTime(2012, 2, 18, 0, 0, 0), new Time(8, 10));
        assertEquals(2, alertCount);
    }

    @Test
    public void shouldReturnElapsedAlertCountWhenStartDateIsTodayAndPreferredTimeIsAfterNow() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        int elapsedAlertCount = alert.getElapsedAlertCount(newDateTime(2012, 2, 20, 0, 0, 0), new Time(8, 20));
        assertEquals(0, elapsedAlertCount);
    }

    @Test
    public void shouldReturnElapsedAlertCountWhenStartDateIsTodayAndAlertTimeIsAfterNow() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        assertEquals(0, alert.getElapsedAlertCount(newDateTime(2012, 2, 20, 8, 20, 0), null));
        assertEquals(10, alert.getRemainingAlertCount(newDateTime(2012, 2, 20, 8, 20, 0), null));
    }

    @Test
    public void shouldNotElapseMoreAlertsThanItsCount() {
        Alert alert = new Alert(days(0), days(1), 3, 0);
        int elapsedAlertCount = alert.getElapsedAlertCount(newDateTime(2012, 2, 15, 0, 0, 0), new Time(8, 20));
        assertEquals(3, elapsedAlertCount);
    }

    @Test
    public void twoAlertsShouldBeElapsed_TestingWithHourUnits() {
        Alert alert = new Alert(hours(0), hours(1), 3, 0);
        assertEquals(2, alert.getElapsedAlertCount(newDateTime(2012, 2, 20, 7, 10, 0), null));
        assertEquals(1, alert.getRemainingAlertCount(newDateTime(2012, 2, 20, 7, 10, 0), null));
    }

    @Test
    public void alertWithAlertTimeAtCurrentTimeIsNotElapsed_TestingWithHourUnits() {
        Alert alert = new Alert(hours(0), hours(1), 3, 0);
        assertEquals(0, alert.getElapsedAlertCount(newDateTime(2012, 2, 20, 8, 15, 0), null));
        assertEquals(3, alert.getRemainingAlertCount(newDateTime(2012, 2, 20, 8, 15, 0), null));
    }

    @Test
    public void shouldGetNextAlertDateTimeAtThePreferredTime() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        DateTime nextAlertDateTime = alert.getNextAlertDateTime(DateUtil.newDateTime(2012, 2, 18, 0, 0, 0), new Time(8, 10));
        assertEquals(DateUtil.newDateTime(2012, 2, 21, 8, 10, 0), nextAlertDateTime);
    }

    @Test
    public void shouldGetNextAlertDateTime() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        DateTime nextAlertDateTime = alert.getNextAlertDateTime(DateUtil.newDateTime(2012, 2, 18, 5, 4, 3), null);
        assertEquals(DateUtil.newDateTime(2012, 2, 21, 5, 4, 3), nextAlertDateTime);
    }

    @Test
    public void alertsForTodayWillBeElapsedIfPreferredTimeIsBeforeCurrentTime() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        int alertCount = alert.getRemainingAlertCount(DateUtil.newDateTime(2012, 2, 18, 0, 0, 0), new Time(8, 10));
        assertEquals(7, alertCount);
    }

    @Test
    public void alertsForTodayWillBeElapsedIfAlertTimeIsBeforeCurrentTime() {
        Alert alert = new Alert(days(0), days(1), 10, 0);
        int alertCount = alert.getRemainingAlertCount(DateUtil.newDateTime(2012, 2, 18, 8, 10, 0), null);
        assertEquals(7, alertCount);
    }
}
