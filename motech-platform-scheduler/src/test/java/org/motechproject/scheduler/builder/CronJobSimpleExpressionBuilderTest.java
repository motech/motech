package org.motechproject.scheduler.builder;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;
import org.quartz.CronExpression;

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

    @Ignore
    @Test
    public void shouldScheduleJobEvery7Days() throws Exception {
        CronExpression cronExpression = new CronExpression("0 0 0 */7 * ?");
        LocalDate today = DateUtil.today();
        LocalDate nextDate = today.plusDays(7);
        LocalDate nextToNextDate = nextDate.plusDays(7);
        System.out.println(cronExpression.getNextValidTimeAfter(DateUtils.addHours(today.toDate(), -1)));
        System.out.println(cronExpression.getNextValidTimeAfter(DateUtils.addHours(nextDate.toDate(), -1)));
        assertEquals(nextDate.toDate(), cronExpression.getNextValidTimeAfter(DateUtils.addHours(today.toDate(), -1)));
        assertEquals(nextToNextDate.toDate(), cronExpression.getNextValidTimeAfter(DateUtils.addHours(nextDate.toDate(), -1)));
    }
}