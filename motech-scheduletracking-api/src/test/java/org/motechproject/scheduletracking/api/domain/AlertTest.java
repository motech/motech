package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
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
        Alert alert = new Alert(new WallTime(0, WallTimeUnit.Day), new WallTime(1, WallTimeUnit.Day), 10, 0);
        int alertCount = alert.getElapsedAlertCount(DateUtil.newDate(2012, 2, 18), new Time(8, 10));
        assertEquals(3, alertCount);
    }

    @Test
    public void shouldReturnElapsedAlertCountWithNonZeroOffset() {
        Alert alert = new Alert(new WallTime(1, WallTimeUnit.Day), new WallTime(1, WallTimeUnit.Day), 10, 0);
        int alertCount = alert.getElapsedAlertCount(DateUtil.newDate(2012, 2, 18), new Time(8, 10));
        assertEquals(2, alertCount);
    }

    @Test
    public void shouldReturnElapsedAlertCountWhenStartDateIsTodayAndPreferredTimeIsBeforeNow() {
        Alert alert = new Alert(new WallTime(0, WallTimeUnit.Day), new WallTime(1, WallTimeUnit.Day), 10, 0);
        int alertCount = alert.getElapsedAlertCount(DateUtil.newDate(2012, 2, 20), new Time(8, 20));
        assertEquals(0, alertCount);
    }

    @Test
    public void shouldNotElapseMoreAlertsThanItsCount() {
        Alert alert = new Alert(new WallTime(0, WallTimeUnit.Day), new WallTime(1, WallTimeUnit.Day), 3, 0);
        int alertCount = alert.getElapsedAlertCount(DateUtil.newDate(2012, 2, 15), new Time(8, 20));
        assertEquals(3, alertCount);
    }

    @Test
    public void shouldGetNextAlertDate() {
        Alert alert = new Alert(new WallTime(0, WallTimeUnit.Day), new WallTime(1, WallTimeUnit.Day), 10, 0);
        DateTime nextAlertDateTime = alert.getNextAlertDateTime(DateUtil.newDate(2012, 2, 18), new Time(8, 10));
        assertEquals(DateUtil.newDateTime(2012, 2, 21, 8, 10, 0), nextAlertDateTime);
    }
}
