package org.motechproject.messagecampaign.builder;

import org.joda.time.LocalDate;
import org.motechproject.messagecampaign.userspecified.CampaignMessageRecord;

import java.util.ArrayList;
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
        campaignMessageRecord.setDate(new LocalDate());
        return campaignMessageRecord;
    }

    public static CampaignMessageRecord createOffsetCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord campaignMessageRecord = createCampaignMessageRecord(name, messageKey);
        campaignMessageRecord.setTimeOffset("1 Week");
        return campaignMessageRecord;
    }

    private static CampaignMessageRecord createCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord record = new CampaignMessageRecord();
        record.setName(name);
        record.setFormats(formats);
        record.setLanguages(languages);
        record.setMessageKey(messageKey);
        record.setStartTime("10:30");
        return record;
    }

    private static CampaignMessageRecord createCampaignMessageRecord(String name, String messageKey, String repeatInterval) {
        CampaignMessageRecord record = new CampaignMessageRecord();
        record.setName(name);
        record.setFormats(formats);
        record.setLanguages(languages);
        record.setMessageKey(messageKey);
        record.setRepeatEvery(repeatInterval);
        record.setStartTime("10:30");
        return record;
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithInterval(String name, String messageKey, String repeatInterval) {
        CampaignMessageRecord record = createCampaignMessageRecord(name, messageKey, repeatInterval);
        record.setStartTime("10:30");
        return record;
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithWeekApplicableDays(String name, String messageKey, List<String> applicableWeekDays) {
        CampaignMessageRecord record = createCampaignMessageRecord(name, messageKey, null);
        record.setRepeatOn(applicableWeekDays);
        record.setStartTime("10:30");
        return record;
    }

    public static CampaignMessageRecord createRepeatingMessageRecordWithCalendarWeek(String name, String messageKey, String calendarStartOfWeek, List<String> applicableWeekDays) {
        CampaignMessageRecord record = createRepeatingMessageRecordWithWeekApplicableDays(name, messageKey, applicableWeekDays);
        record.setStartTime("10:30");
        return record;
    }

    public static CampaignMessageRecord createCronBasedCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord record = createCampaignMessageRecord(name, messageKey);
        record.setStartTime("10:30");
        record.setCron("0 11 11 11 11 ?");
        return record;
    }
}
