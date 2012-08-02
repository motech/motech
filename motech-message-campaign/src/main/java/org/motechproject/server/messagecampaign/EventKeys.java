package org.motechproject.server.messagecampaign;

public final class EventKeys {

    public static final String SCHEDULE_JOB_ID_KEY = "JobID";
    public static final String CAMPAIGN_NAME_KEY = "CampaignName";
    public static final String MESSAGE_NAME_KEY = "MessageName";
    public static final String MESSAGE_FORMATS = "MessageFormats";
    public static final String MESSAGE_LANGUAGES = "MessageLanguages";
    public static final String EXTERNAL_ID_KEY = "ExternalID";
    public static final String MESSAGE_KEY = "MessageKey";    
    public static final String GENERATED_MESSAGE_KEY = "GenMsgKey";

    public static final String BASE_SUBJECT = "org.motechproject.server.messagecampaign.";
    public static final String SEND_MESSAGE = BASE_SUBJECT + "fired-campaign-message";
    public static final String CAMPAIGN_COMPLETED = BASE_SUBJECT + "campaign-completed";

    private EventKeys() {
    }
}
