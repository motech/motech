package org.motechproject.metrics.model;

/**
 * Implementation of a snapshot of a histogram.
 */
public class SnapshotAdapter implements org.motechproject.metrics.api.Snapshot {
    private final com.codahale.metrics.Snapshot snapshot;

    public SnapshotAdapter(com.codahale.metrics.Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Returns a value at the given quantile.
     *
     * @param quantile the given quantile in the range [0..1]
     *
     * @return the value in the distribution at the given quantile
     */
    @Override
    public double getValue(double quantile) {
        return snapshot.getValue(quantile);
    }

    /**
     * Returns the distribution's entire set of values.
     *
     * @return the entire set of values
     */
    @Override
    public long[] getValues() {
        return snapshot.getValues();
    }

    /**
     * Returns the number of values in the snapshot.
     *
     * @return the number of values in the snapshot
     */
    @Override
    public int size() {
        return snapshot.size();
    }

    /**
     * Returns the distribution's median value.
     *
     * @return the distribution's median value
     */
    @Override
    public double getMedian() {
        return snapshot.getMedian();
    }

    /**
     * Returns the value at the distribution's 75th percentile.
     *
     * @return the value at the 75th percentile
     */
    @Override
    public double get75thPercentile() {
        return snapshot.get75thPercentile();
    }

    /**
     * Returns the value at the distribution's 95th percentile.
     *
     * @return the value at the 95th percentile
     */
    @Override
    public double get95thPercentile() {
        return snapshot.get95thPercentile();
    }

    /**
     * Returns the value at the distribution's 99th percentile.
     *
     * @return the value at the 99th percentile
     */
    @Override
    public double get99thPercentile() {
        return snapshot.get99thPercentile();
    }

    /**
     * Returns the value at the distribution's 99.9th percentile.
     *
     * @return the value at the 99.9th percentile
     */
    @Override
    public double get999thPercentile() {
        return snapshot.get999thPercentile();
    }

    /**
     * Returns the distribution's maximum value.
     *
     * @return the distribution's maximum value.
     */
    @Override
    public long getMax() {
        return snapshot.getMax();
    }

    /**
     * Returns the distribution's mean value.
     *
     * @return the distribution's mean value
     */
    @Override
    public double getMean() {
        return snapshot.getMean();
    }

    /**
     * Return's the distribution's minimum value.
     *
     * @return the distribution's minimum value
     */
    @Override
    public long getMin() {
        return snapshot.getMin();
    }

    /**
     * Returns the standard deviation of the distribution's values.
     *
     * @return the standard deviation
     */
    @Override
    public double getStdDev() {
        return snapshot.getStdDev();
    }
}
