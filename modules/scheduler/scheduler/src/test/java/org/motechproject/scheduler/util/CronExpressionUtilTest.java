package org.motechproject.scheduler.util;

import org.junit.Test;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.exception.CronExpressionException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.motechproject.commons.date.model.DayOfWeek.Monday;
import static org.motechproject.commons.date.model.DayOfWeek.Saturday;
import static org.motechproject.commons.date.model.DayOfWeek.Sunday;
import static org.motechproject.commons.date.model.DayOfWeek.Tuesday;
import static org.motechproject.commons.date.model.DayOfWeek.Wednesday;

public class CronExpressionUtilTest {

    @Test
    public void shouldParseValidCronExpression() {
        String expression = "0 25 13 ? * 1,2,3,4,7";
        Time expectedTime = new Time(13, 25);
        List<DayOfWeek> expectedDaysOfWeek = prepareExpectedDaysOfWeek();

        CronExpressionUtil util = new CronExpressionUtil(expression);

        assertThat(util.getTime(), equalTo(expectedTime));
        assertThat(util.getDaysOfWeek(), equalTo(expectedDaysOfWeek));
    }

    @Test(expected = CronExpressionException.class)
    public void shouldNotParseInvalidCronExpression() {
        new CronExpressionUtil("0 25 13 * * 1,2,3,4,5");
    }

    private List<DayOfWeek> prepareExpectedDaysOfWeek() {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        daysOfWeek.add(Sunday);
        daysOfWeek.add(Monday);
        daysOfWeek.add(Tuesday);
        daysOfWeek.add(Wednesday);
        daysOfWeek.add(Saturday);
        return daysOfWeek;
    }

}