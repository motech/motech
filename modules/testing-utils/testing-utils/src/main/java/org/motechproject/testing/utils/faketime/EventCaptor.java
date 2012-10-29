package org.motechproject.testing.utils.faketime;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.quartz.Scheduler;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerThread;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.junit.Assert.fail;
import static org.motechproject.util.DateUtil.now;
import static org.springframework.util.CollectionUtils.isEmpty;

public class EventCaptor implements EventListener {

    private static final long DELAY_THRESHOLD = 5000;
    public static final int STEP_BACK = 20;
    public static final int EVENT_TIMEOUT = 100;

    private List<DateTime> eventTimes = new ArrayList<DateTime>();

    private QuartzSchedulerThread quartzSchedulerThread;
    private Object sigLock;
    private String name;

    public EventCaptor(String name, Scheduler scheduler) {
        this.name = name;
        try {
            QuartzScheduler quartzScheduler = accessPrivateField(scheduler, "sched", QuartzScheduler.class);
            quartzSchedulerThread = accessPrivateField(quartzScheduler, "schedThread", QuartzSchedulerThread.class);
            sigLock = accessPrivateField(quartzSchedulerThread, "sigLock", Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public void handle(MotechEvent event) {
        eventTimes.add(now());
        synchronized (sigLock) {
            sigLock.notifyAll();
        }
    }

    public void assertEventRaisedAt(List<DateTime> expectedTimes) {
        for (DateTime time : expectedTimes) {
            int eventsRaised = eventTimes.size();
            //System.moveTimeBy(time.getMillis() - now().getMillis() - STEP_BACK);
            try {
                waitForEvent(eventsRaised);
            } catch (EventTimeoutException e) {
                fail(format("No event raised at %s.\n%s", time, eventLog(expectedTimes, eventTimes)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEventTimes(expectedTimes, eventTimes);
    }

    private void waitForEvent(int eventsRaised) throws EventTimeoutException, InterruptedException {
        long start = System.currentTimeMillis();
        while (eventTimes.size() <= eventsRaised) {
            quartzSchedulerThread.signalSchedulingChange(0);
            synchronized (sigLock) {
                sigLock.wait(10);
            }
            if (System.currentTimeMillis() > start + (eventsRaised > 0? EVENT_TIMEOUT : 1000)) {  // first trigger seems to take longer
                throw new EventTimeoutException();
            }
        }
    }

    private void assertEventTimes(List<DateTime> expectedTimes, List<DateTime> eventTimes) {
        if (expectedTimes.size() != eventTimes.size()) {
            fail(format("Expected %s events, got %s.\n%s", expectedTimes.size(), eventTimes.size(), eventLog(expectedTimes, eventTimes)));
        }
        for (int i = 0; i < expectedTimes.size(); i++) {
            if (expectedTimes.get(i).getMillis() - eventTimes.get(i).getMillis() > DELAY_THRESHOLD) {
                fail(format("No event raised at %s.\n%s", expectedTimes.get(i), eventLog(expectedTimes, eventTimes)));
            }
        }
    }

    private <T> T accessPrivateField(Object scheduler, String fieldName, Class<T> returnType) {
        try {
            Field schedField = scheduler.getClass().getDeclaredField(fieldName);
            schedField.setAccessible(true);
            return returnType.cast(schedField.get(scheduler));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    class EventTimeoutException extends Exception {
    }

    private String eventLog(List<DateTime> expectedTimes, List<DateTime> eventTimes) {
        return format("Expected times: [%s]\n   Event times: [%s]", printDateTimes(expectedTimes), printDateTimes(eventTimes));
    }

    private String printDateTimes(List<DateTime> times) {
        String s = "";
        if (!isEmpty(times)) {
            for (int i = 0; i < times.size(); i++) {
                s += times.get(i);
                if (i < times.size() - 1) {
                    s += ", ";
                }
            }
        }
        return s;
    }
}
