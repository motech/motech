package org.motechproject.server.pillreminder.util;


import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

import java.util.Date;

public class PillReminderTime {

    public int timesPillRemindersSent(Dosage dosage, int pillWindow, int retryInterval) {
        Time dosageStartTime = dosage.getStartTime();
        DateTime now = new DateTime();

        int differenceBetweenDosageStartTimeAndNowInHours = getOffsetOfCurrentTimeFromDosageStartTime(dosageStartTime, now);
        int differenceBetweenDosageStartTimeAndNowInMinutes = differenceBetweenDosageStartTimeAndNowInHours * 60;

        return (differenceBetweenDosageStartTimeAndNowInMinutes / retryInterval);
    }

    public int timesPillRemainderWillBeSent(int pillWindow, int retryInterval) {
        return (pillWindow * 60) / retryInterval;
    }

    private int getOffsetOfCurrentTimeFromDosageStartTime(Time dosageStartTime, DateTime now) {
        int hourDifference = now.getHourOfDay() - dosageStartTime.getHour();
        return hourDifference > 0 ? hourDifference : 0;
    }

    public boolean isDosageTaken(Dosage dosage, int pillWindow) {
        DateTime dosageConsumedDate = new DateTime(dosage.getCurrentDosageDate());

        DateTime windowStartTime = new DateTime()
                .withHourOfDay(dosage.getStartTime().getHour())
                .withMinuteOfHour(dosage.getStartTime().getMinute());

        DateTime windowEndTime = windowStartTime.plusHours(pillWindow);

        return dosageConsumedDate.isAfter(windowStartTime) && dosageConsumedDate.isBefore(windowEndTime);
    }
}
