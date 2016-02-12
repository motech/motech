package org.motechproject.metrics.api;

/**
 * Represents a statistical snapshot.
 */
public interface Snapshot {
    /**
     * Returns a value at the given quantile.
     *
     * @param quantile the given quantile in the range [0..1]
     *
     * @return the value in the distribution at the given quantile
     */
    double getValue(double quantile);

    /**
     * Returns the distribution's entire set of values.
     *
     * @return the entire set of values
     */
    long[] getValues();

    /**
     * Returns the number of values in the snapshot.
     *
     * @return the number of values in the snapshot
     */
    int size();

    /**
     * Returns the distribution's median value.
     *
     * @return the distribution's median value
     */
    double getMedian();

    /**
     * Returns the value at the distribution's 75th percentile.
     *
     * @return the value at the 75th percentile
     */
    double get75thPercentile();

    /**
     * Returns the value at the distribution's 95th percentile.
     *
     * @return the value at the 95th percentile
     */
    double get95thPercentile();

    /**
     * Returns the value at the distribution's 99th percentile.
     *
     * @return the value at the 99th percentile
     */
    double get99thPercentile();

    /**
     * Returns the value at the distribution's 99.9th percentile.
     *
     * @return the value at the 99.9th percentile
     */
    double get999thPercentile();

    /**
     * Returns the distribution's maximum value.
     *
     * @return the distribution's maximum value.
     */
    long getMax();

    /**
     * Returns the distribution's mean value.
     *
     * @return the distribution's mean value
     */
    double getMean();

    /**
     * Return's the distribution's minimum value.
     *
     * @return the distribution's minimum value
     */
    long getMin();

    /**
     * Returns the standard deviation of the distribution's values.
     *
     * @return the standard deviation
     */
    double getStdDev();
}
