package org.motechproject.builder;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CronJobExpressionBuilderTest {
    @Test
    public void testBuild_case1() {
        String cronJobExpression = new CronJobExpressionBuilder(9, 5, 5, 20).build();
        assertEquals("0 5/20 9-11 * * ?", cronJobExpression);
    }

    @Test
    public void testBuild_case2() {
        String cronJobExpression = new CronJobExpressionBuilder(9, 0, 4, 30).build();
        assertEquals("0 0/30 9-11 * * ?", cronJobExpression);
    }

    @Test
    public void testBuild_case3() {
        String cronJobExpression = new CronJobExpressionBuilder(9, 15, 3, 15).build();
        assertEquals("0 15/15 9-10 * * ?", cronJobExpression);
    }

    @Test
    public void testBuild_case4() {
        String cronJobExpression = new CronJobExpressionBuilder(9, 15, 0, 0).build();
        assertEquals("0 15 9 * * ?", cronJobExpression);
    }

    @Test
    public void testBuild_case5() {
        String cronJobExpression = new CronJobExpressionBuilder(9, 0, 0, 0).build();
        assertEquals("0 0 9 * * ?", cronJobExpression);
    }
}
