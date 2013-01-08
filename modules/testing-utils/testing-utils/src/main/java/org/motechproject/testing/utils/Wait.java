package org.motechproject.testing.utils;

public class Wait {
    private final Object lock;
    private final WaitCondition condition;
    private int maxWaitTime;
    private int waitDurationBetweenChecks;

    public Wait(Object lock, WaitCondition condition, int waitDurationBetweenChecks, int maxWaitTime) {
        this.lock = lock;
        this.condition = condition;
        this.maxWaitTime = maxWaitTime;
        this.waitDurationBetweenChecks = waitDurationBetweenChecks;
    }

    public Wait(Object lock, int waitTime) {
        this(lock, new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return true;
            }
        }, waitTime, waitTime);
    }

    public void start() throws InterruptedException {
        int waitingFor = 0;
        synchronized (lock) {
            while (condition.needsToWait() && waitingFor <= maxWaitTime) {
                lock.wait(waitDurationBetweenChecks);
                waitingFor = waitingFor + waitDurationBetweenChecks;
            }
        }
    }
}
