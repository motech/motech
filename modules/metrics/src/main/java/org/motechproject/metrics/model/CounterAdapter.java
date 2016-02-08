package org.motechproject.metrics.model;


import org.motechproject.metrics.api.Counter;

/**
 * A counter implementation that can be enabled or disabled depending on configuration settings.
 */
public class CounterAdapter implements Counter, Enablable {
    private final com.codahale.metrics.Counter counter;
    private boolean isEnabled;

    public CounterAdapter(com.codahale.metrics.Counter counter, boolean enabled) {
        this.counter = counter;
        isEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * If metrics are enabled, increment the counter by one. Otherwise, do nothing.
     */
    @Override
    public void inc() {
        if (isEnabled()) {
            counter.inc();
        }
    }

    /**
     * If metrics are enabled, increment the counter by the provided value. Otherwise, do nothing.
     * @param n the value to increment
     */
    @Override
    public void inc(long n) {
        if (isEnabled()) {
            counter.inc(n);
        }
    }

    /**
     * If metrics are enabled, decrement the counter by one. Otherwise, do nothing.
     */
    @Override
    public void dec() {
        if (isEnabled()) {
            counter.dec();
        }
    }

    /**
     * If metrics are enabled, decrement the counter by the provided value. Otherwise, do nothing.
     *
     * @param n the value to decrement
     */
    @Override
    public void dec(long n) {
        if (isEnabled()) {
            counter.dec(n);
        }
    }

    /**
     * Get the current count
     *
     * @return the count
     */
    @Override
    public long getCount() {
        return counter.getCount();
    }
}
