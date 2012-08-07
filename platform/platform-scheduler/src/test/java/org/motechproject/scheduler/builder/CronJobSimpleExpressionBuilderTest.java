package org.motechproject.scheduler.builder;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.quartz.SimpleTrigger;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

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
        SimpleTrigger trigger = newTrigger()
                .withIdentity(triggerKey("triggerName", "groupName"))
                .startNow()
                .withSchedule(simpleSchedule()
                    .withIntervalInHours(7 * 24)
                    .repeatForever())
                .build();

        LocalDate today = DateUtil.today();
        Date yesterday = today.plusDays(-1).toDate();

        Date firstFireTime = trigger.getFireTimeAfter(yesterday);
        Date secondFireTime = trigger.getFireTimeAfter(firstFireTime);
        Date thirdFireTime = trigger.getFireTimeAfter(secondFireTime);

        assertEquals(Duration.standardDays(7).getMillis(), secondFireTime.getTime() - firstFireTime.getTime());
        assertEquals(Duration.standardDays(7).getMillis(), thirdFireTime.getTime() - secondFireTime.getTime());
    }
}