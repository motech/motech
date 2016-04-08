package org.motechproject.commons.api.metrics;

import java.text.DecimalFormat;


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


    private void resetTimer() {
        start = System.currentTimeMillis();
    }


    public Timer() {
        resetTimer();
    }


    public Timer(String obj, String objs) {
        this();
        this.obj = obj;
        this.objs = objs;
    }


    public void reset() {
        resetTimer();
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


    public String time() {
        return durationString(System.currentTimeMillis() - start);
    }


    public String millis() {
        return milliString(System.currentTimeMillis() - start);
    }


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
}
