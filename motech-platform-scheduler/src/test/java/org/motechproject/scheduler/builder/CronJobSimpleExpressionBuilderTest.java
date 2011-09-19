package org.motechproject.scheduler.builder;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.quartz.SimpleTrigger;

import static org.junit.Assert.assertEquals;

public class CronJobSimpleExpressionBuilderTest {
    @Test
    public void testBuild() {
        CronJobSimpleExpressionBuilder builder = new CronJobSimpleExpressionBuilder(new Time(10, 25));
        assertEquals("0 25 10 * * ?", builder.build());
    }

    @Test
    public void shouldBuildValidCronExpressionWithDayRepeat() {
        String expression = new CronJobSimpleExpressionBuilder(new Time(10, 25)).withRepeatIntervalInDays(7).build();
        assertEquals("0 25 10 */7 * ?", expression);
    }

    @Test
    public void shouldScheduleJobEverySevenDays() {
        SimpleTrigger trigger = new SimpleTrigger("triggerName", "groupName", DateUtil.today().toDate(), null, SimpleTrigger.REPEAT_INDEFINITELY, Duration.standardDays(7).getMillis());
        LocalDate today = DateUtil.today();
        LocalDate nextDate = today.plusDays(7);
        LocalDate nextToNextDate = nextDate.plusDays(7);

        assertEquals(nextDate.toDate(), trigger.getFireTimeAfter(today.toDate()));
        assertEquals(nextToNextDate.toDate(), trigger.getFireTimeAfter(nextDate.toDate()));
    }

}