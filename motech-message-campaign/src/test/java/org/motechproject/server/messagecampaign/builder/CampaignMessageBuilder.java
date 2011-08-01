package org.motechproject.server.messagecampaign.builder;

import org.motechproject.server.messagecampaign.userspecified.CampaignMessageRecord;

import java.util.ArrayList;
import java.util.Date;

public class CampaignMessageBuilder {
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
        return new CampaignMessageRecord().name(name).formats(formats).languages(languages).messageKey(messageKey);
    }

    public static CampaignMessageRecord createRepeatingCampaignMessageRecord(String name, String messageKey) {
        return createOffsetCampaignMessageRecord(name, messageKey);
    }

    public static CampaignMessageRecord createCronBasedCampaignMessageRecord(String name, String messageKey) {
        CampaignMessageRecord campaignMessageRecord = createCampaignMessageRecord(name, messageKey);
        campaignMessageRecord.cron("0 11 11 11 11 ?");
        return campaignMessageRecord;
    }
}
