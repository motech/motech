package org.motechproject.metrics.model;

import org.motechproject.metrics.api.Snapshot;
import org.motechproject.metrics.api.Timer;

import java.util.concurrent.TimeUnit;

/**
 * A timer implementation that can be enabled or disabled depending on configuration settings.
 */
public class TimerAdapter implements Timer, Enablable {
    private final com.codahale.metrics.Timer timer;
    private boolean isEnabled;

    private static final class Context implements org.motechproject.metrics.api.Timer.Context {
        private final com.codahale.metrics.Timer.Context context;

        private Context(com.codahale.metrics.Timer.Context context) {
            this.context = context;
        }

        @Override
        public void stop() {
            this.context.stop();
        }
    }

    private static final org.motechproject.metrics.api.Timer.Context DUMMY_CONTEXT = new org.motechproject.metrics.api.Timer.Context() {
        @Override
        public void stop() {}
    };

    public TimerAdapter(com.codahale.metrics.Timer timer, boolean enabled) {
        this.timer = timer;
        this.isEnabled = enabled;
    }

    /**
     * If metrics is enabled, then update the timer with the provided duration, otherwise, do nothing.
     *
     * @param duration the length of the duration
     * @param unit the time unit of the duration
     */
    @Override
    public void update(long duration, TimeUnit unit) {
        if (isEnabled()) {
            timer.update(duration, unit);
        }
    }

    /**
     * If metrics is enabled, then return a functional instance of a timer context, otherwise, return a dummy instance
     * with no-op methods.
     *
     * @return the timer context
     */
    @Override
    public org.motechproject.metrics.api.Timer.Context time() {
        if (isEnabled()) {
            return new Context(timer.time());
        } else {
            return DUMMY_CONTEXT;
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public long getCount() {
        return timer.getCount();
    }

    @Override
    public double getFifteenMinuteRate() {
        return timer.getFifteenMinuteRate();
    }

    @Override
    public double getFiveMinuteRate() {
        return timer.getFiveMinuteRate();
    }

    @Override
    public double getMeanRate() {
        return timer.getMeanRate();
    }

    @Override
    public double getOneMinuteRate() {
        return timer.getOneMinuteRate();
    }

    @Override
    public Snapshot getSnapshot() {
        return new SnapshotAdapter(timer.getSnapshot());
    }
}
