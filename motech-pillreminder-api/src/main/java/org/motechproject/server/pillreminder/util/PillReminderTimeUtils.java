    package org.motechproject.server.pillreminder.util;


import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

public class PillReminderTimeUtils {

    public int timesPillRemindersSent(Dosage dosage, int pillWindowInHours, int retryInterval) {
        Time dosageStartTime = dosage.getDosageTime();
        DateTime now = Util.currentDateTime();
        int minsSinceDosage = Math.min(getOffsetOfCurrentTimeFromDosageStartTime(dosageStartTime, now), pillWindowInHours * 60);
        return (minsSinceDosage / retryInterval);
    }

    public int timesPillRemainderWillBeSent(int pillWindow, int retryInterval) {
        return (pillWindow * 60) / retryInterval;
    }

    private int getOffsetOfCurrentTimeFromDosageStartTime(Time dosageStartTime, DateTime now) {
        int hourDiff = now.getHourOfDay() - dosageStartTime.getHour();
        if (hourDiff < 0) hourDiff += 24;
        return hourDiff * 60  + now.getMinuteOfHour() - dosageStartTime.getMinute();
    }

    public boolean isDosageTaken(Dosage dosage, int pillWindow) {
        DateTime dosageConsumedDate = new DateTime(dosage.getResponseLastCapturedDate());

        DateTime windowStartTime = new DateTime()
                .withHourOfDay(dosage.getDosageTime().getHour())
                .withMinuteOfHour(dosage.getDosageTime().getMinute());

        DateTime windowEndTime = windowStartTime.plusHours(pillWindow);

        return dosageConsumedDate.isAfter(windowStartTime) && dosageConsumedDate.isBefore(windowEndTime);
    }
}
