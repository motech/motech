package org.motechproject.server.messagecampaign;

public class EventKeys {

    public final static String SCHEDULE_JOB_ID_KEY = "JobID";
    public final static String CAMPAIGN_NAME_KEY = "CampaignName";
    public final static String MESSAGE_NAME_KEY = "MessageName";
    public final static String MESSAGE_FORMATS = "MessageFormats";
    public final static String MESSAGE_LANGUAGES = "MessageLanguages";
    public final static String EXTERNAL_ID_KEY = "ExternalID";
    public static final String MESSAGE_KEY = "MessageKey";    
    public static final String GENERATED_MESSAGE_KEY = "GenMsgKey";

    public final static String BASE_SUBJECT = "org.motechproject.server.messagecampaign.";
    public final static String SEND_MESSAGE = BASE_SUBJECT + "fired-campaign-message";
    public final static String CAMPAIGN_COMPLETED = BASE_SUBJECT + "campaign-completed";

}
