package org.motechproject.scheduler.domain;

import org.junit.Test;
import org.motechproject.scheduler.exception.MotechSchedulerException;

import static org.junit.Assert.assertEquals;


public class TestMotechSchedulerException {
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
