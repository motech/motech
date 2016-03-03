package org.motechproject.testing.osgi.wait;

/**
 * A utility for waiting. Waiting can(but doesn't have to) happen on a specified lock.
 * Moreover custom wait conditions can be provided.
 */
public class Wait {
    private static final int DEFAULT_WAIT_DURATION = 100;
    private final Object lock;
    private final WaitCondition condition;
    private int maxWaitTime;
    private int waitDurationBetweenChecks;

    /**
     * Creates a wait object that can be then used for starting a wait.
     * @param lock the lock used for java wait's
     * @param condition an implementation of {@link WaitCondition} that will be used for waiting
     * @param waitDurationBetweenChecks the wait duration between checking the condition in milliseconds
     * @param maxWaitTime the maximum overall wait time in millisecond
     */
    public Wait(Object lock, WaitCondition condition, int waitDurationBetweenChecks, int maxWaitTime) {
        this.lock = lock;
        this.condition = condition;
        this.maxWaitTime = maxWaitTime;
        this.waitDurationBetweenChecks = recalibrateWaitDurationBetweenChecks(waitDurationBetweenChecks, maxWaitTime);
    }

    /**
     * Creates a wait object that can be then used for waiting for a period of time.
     * @param lock the lock used for java wait's
     * @param waitTime the time to wait in milliseconds
     */
    public Wait(Object lock, int waitTime) {
        this(lock, new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return true;
            }
        }, waitTime, waitTime);
    }

    /**
     * Creates a wait object that can be then used for waiting for a period of time. Wait interval of 100 milliseconds
     * will be used between checks to the wait condition.
     * @param waitCondition the condition that will be used for checking whether to continue waiting
     * @param waitTime the time to wait in milliseconds
     */
    public Wait(WaitCondition waitCondition, int waitTime) {
        this(new Object(), waitCondition, waitTime, waitTime);
    }

    /**
     * Starts the wait.
     * @throws InterruptedException if the wait was interrupted (someone called notify on the lock)
     */
    public void start() throws InterruptedException {
        int waitingFor = 0;
        synchronized (lock) {
            while (condition.needsToWait() && waitingFor <= maxWaitTime) {
                lock.wait(waitDurationBetweenChecks);
                waitingFor = waitingFor + waitDurationBetweenChecks;
            }
        }
    }

    private int recalibrateWaitDurationBetweenChecks(int waitDurationBetweenChecks, int maxWaitTime) {
        return waitDurationBetweenChecks >= maxWaitTime ? getWaitDurationBetweenChecks(maxWaitTime) : maxWaitTime;
    }

    private int getWaitDurationBetweenChecks(int maxWaitTime) {
        return maxWaitTime < DEFAULT_WAIT_DURATION ? maxWaitTime : DEFAULT_WAIT_DURATION;
    }
}
