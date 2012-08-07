package org.motechproject.server.event;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EventHandlerForServerEventRelayTransactionIT {
    public static final String FAILING_EVENT_SUBJECT = "FailingEventSubject";
    public static final String SUCCESSFUL_EVENT_SUBJECT = "SuccessfulEventSubject";
    public static final String LONG_RUNNING_PROCESS = "LongRunningProcess";
    public static final int TASK_DURATION = 3;

    private boolean doThrowException;
    private int retries;

    public EventHandlerForServerEventRelayTransactionIT setupForFailure(boolean doThrowException) {
        this.doThrowException = doThrowException;
        retries = 0;
        return this;
    }

    @MotechListener(subjects = {FAILING_EVENT_SUBJECT})
    public void canFail(MotechEvent motechEvent) {
        retries++;
        if (doThrowException) throw new RuntimeException();
    }

    public int retries() {
        return retries;
    }

    @MotechListener(subjects = {SUCCESSFUL_EVENT_SUBJECT})
    public void wouldPass(MotechEvent motechEvent) {
    }

    @MotechListener(subjects = {LONG_RUNNING_PROCESS})
    public void handleLongRunningProcess(MotechEvent motechEvent) throws Exception{
        System.out.println(new Date() + "|" +  Thread.currentThread().getId() + " handleLongRunningProcess start");
        Thread.sleep(1000* TASK_DURATION);
        System.out.println(new Date() + "|" +  Thread.currentThread().getId() + " handleLongRunningProcess end");
    }
}
