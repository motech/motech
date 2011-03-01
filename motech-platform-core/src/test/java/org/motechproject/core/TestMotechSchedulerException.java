package org.motechproject.core;

import org.junit.Test;
import org.motechproject.scheduler.MotechSchedulerException;

import static org.junit.Assert.assertEquals;


/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 3/1/11
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestMotechSchedulerException
{
    @Test
    public void newTest() throws Exception {
        String msg = "message";
        MotechSchedulerException ex = new MotechSchedulerException(msg);
        assertEquals(msg, ex.getMessage());

        ex = new MotechSchedulerException(msg, new Throwable());
        assertEquals(msg, ex.getMessage());

        Throwable t = new Throwable(msg);
        msg = t.toString();
        ex = new MotechSchedulerException(t);
        assertEquals(msg, ex.getMessage());
    }
}