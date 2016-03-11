package org.motechproject.scheduler.util;

import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduler.exception.CronExpressionException;
import org.quartz.CronExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for extracting information from the given cron expression.
 */
public class CronExpressionUtil {

    private Time time;

    private List<DayOfWeek> daysOfWeek;

    public CronExpressionUtil(String expression) {
        if (!CronExpression.isValidExpression(expression)) {
            throw new CronExpressionException(expression);
        }

        String[] parts = expression.split(" ");

        time = new Time(Integer.valueOf(parts[2]), Integer.valueOf(parts[1]));

        daysOfWeek = new ArrayList<>();
        for (String part : parts[5].split(",")) {
            daysOfWeek.add(DayOfWeek.getDayOfWeek(getDayOfWeekValue(part)));
        }
    }

    public Time getTime() {
        return time;
    }

    public List<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    private int getDayOfWeekValue(String cronValue) {
        int value = Integer.valueOf(cronValue);
        return value == 1 ? 7 : value - 1;
    }
}
