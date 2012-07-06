package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.userspecified.CampaignMessageRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CampaignMessageRecordTestBuilder {
    private static ArrayList<String> formats = new ArrayList<String>() {{
        add("SMS");
        add("IVR");
    }};

    private static ArrayList<String> languages = new ArrayList<String>() {{
        add("en");
        add("gh");
    }};

    public static CampaignMessageRecord createAbsoluteCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord campaignMessageRecord = createCampaignMessageRecord(name, messageKey);
        campaignMessageRecord.date(new Date());
        return campaignMessageRecord;
    }

    public static CampaignMessageRecord createOffsetCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord campaignMessageRecord = createCampaignMessageRecord(name, messageKey);
        campaignMessageRecord.timeOffset("1 Week");
        return campaignMessageRecord;
    }

    private static CampaignMessageRecord createCampaignMessageRecord(String name, String messageKey) {
        return new CampaignMessageRecord().name(name).formats(formats).languages(languages).messageKey(messageKey).startTime("10:30");
    }

    private static CampaignMessageRecord createCampaignMessageRecord(String name, String messageKey, String repeatInterval) {
        return new CampaignMessageRecord().name(name).formats(formats).languages(languages).messageKey(messageKey).repeatEvery(repeatInterval).startTime("10:30");
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithInterval(String name, String messageKey, String repeatInterval) {
        return createCampaignMessageRecord(name, messageKey, repeatInterval).startTime("10:30");
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithWeekApplicableDays(String name, String messageKey, List<String> applicableWeekDays) {
        return createCampaignMessageRecord(name, messageKey, null).repeatOn(applicableWeekDays).startTime("10:30");
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithCalendarWeek(String name, String messageKey, String calendarStartOfWeek, List<String> applicableWeekDays) {
        return createRepeatingMessageRecordWithWeekApplicableDays(name, messageKey, applicableWeekDays).startTime("10:30");
    }

    public static CampaignMessageRecord createCronBasedCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord campaignMessageRecord = createCampaignMessageRecord(name, messageKey).startTime("10:30");
        campaignMessageRecord.cron("0 11 11 11 11 ?");
        return campaignMessageRecord;
    }
}
