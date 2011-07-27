package org.motechproject.server.pillreminder.util;


import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.domain.Dosage;

public class PillReminderTime {

     public int timesPillRemindersSent(Dosage dosage, int pillWindow, int retryInterval) {
        Time dosageStartTime = dosage.getStartTime();
        DateTime now = new DateTime();

        int differenceBetweenDosageStartTimeAndNowInHours = getOffsetOfCurrentTimeFromDosageStartTime(dosageStartTime, now);
        int differenceBetweenDosageStartTimeAndNowInMinutes = differenceBetweenDosageStartTimeAndNowInHours * 60;

        return (differenceBetweenDosageStartTimeAndNowInMinutes / retryInterval);
    }

    public int timesPillRemainderWillBeSent(int pillWindow, int retryInterval) {
        return (pillWindow * 60)/retryInterval;
    }

    private int getOffsetOfCurrentTimeFromDosageStartTime(Time dosageStartTime, DateTime now) {
        int hourDifference = now.getHourOfDay() - dosageStartTime.getHour();
        return hourDifference > 0? hourDifference: 0;
    }
}
