package org.motechproject.scheduler.builder;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.quartz.CronExpression;

import java.util.Date;

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
    public void shouldScheduleJobEvery7Days() throws Exception {
        CronExpression cronExpression = new CronExpression("0 0 0 */7 * ?");
        Date now = new Date();
        Date nextValidTimeAfter = cronExpression.getNextValidTimeAfter(now);
        assertEquals(nextValidTimeAfter, DateUtil.today().plusDays(7).toDate());
    }
}