package org.motechproject.commons.api.metrics;

import java.text.DecimalFormat;

/**
 * Class for displaying information about measured time and frequency.
 */
public class Timer {

    private static final DecimalFormat FMT_SEC = new DecimalFormat("#,##0.000");
    private static final DecimalFormat FMT_INT = new DecimalFormat("#,##0");
    private static final DecimalFormat FMT_DEC = new DecimalFormat("#,##0.000");

    private static final long MILLIS_PER_HOUR = 3600000L;
    private static final long MILLIS_PER_MIN = 60000L;
    private static final double MILLIS_PER_SEC = 1000.0;

    private long start;
    private String obj;
    private String objs;

    /**
     * Constructor.
     */
    public Timer() {
        resetTimer();
    }

    /**
     * Constructor.
     * @param obj - singular noun of counted object
     * @param objs - plural form of counted object
     */
    public Timer(String obj, String objs) {
        this();
        this.obj = obj;
        this.objs = objs;
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        resetTimer();
    }

    /**
     * Returns information about measured time.
     * @return difference between the current and initial time in "hours minutes seconds" format
     */
    public String time() {
        return durationString(System.currentTimeMillis() - start);
    }

    /**
     * Returns information about measured time in milliseconds.
     * @return difference between the current and initial time in milliseconds
     */
    public String millis() {
        return milliString(System.currentTimeMillis() - start);
    }

    /**
     * Returns information about frequency.
     * @param count how many objects were counted
     * @return information about how many objects were counted between the current and initial time and frequency
     */
    public String frequency(long count) {
        long duration = System.currentTimeMillis() - start;
        double freq = (count * MILLIS_PER_SEC) / ((duration) * 1.0);

        return String.format(
                "%s %s in %s at %s%s/s",
                FMT_INT.format(count),
                count == 1 ? obj : objs,
                durationString(duration),
                FMT_DEC.format((count * MILLIS_PER_SEC) / ((duration) * 1.0)),
                freq == 1.0 ? obj : objs
        );
    }

    private void resetTimer() {
        start = System.currentTimeMillis();
    }

    private String milliString(long duration) {
        return String.format("%sms", FMT_INT.format(duration));
    }

    private String durationString(long duration) {
        long millis = duration;
        long hours = millis / MILLIS_PER_HOUR;
        millis -= hours * MILLIS_PER_HOUR;
        long minutes = millis / MILLIS_PER_MIN;
        millis -= minutes * MILLIS_PER_MIN;
        double seconds = millis / MILLIS_PER_SEC;

        if (hours > 0) {
            return String.format("%dh %dm %ss", hours, minutes, FMT_SEC.format(seconds));
        }

        if (minutes > 0) {
            return String.format("%dm %ss", minutes, FMT_SEC.format(seconds));
        }

        if (millis > MILLIS_PER_SEC) {
            return String.format("%ss", FMT_SEC.format(seconds));
        }

        return milliString(millis);
    }
}
