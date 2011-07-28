package org.motechproject.server.messagecampaign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventKeys {
	private final static Logger log = LoggerFactory.getLogger(EventKeys.class);

    public final static String SCHEDULE_JOB_ID_KEY = "JobID";
    public final static String CAMPAIGN_NAME_KEY = "CampaignName";
	public final static String EXTERNAL_ID_KEY = "ExternalID";

    public final static String BASE_SUBJECT = "org.motechproject.server.messagecampaign.";
    public final static String MESSAGE_CAMPAIGN_EVENT_SUBJECT = BASE_SUBJECT + "scheduler-reminder";
}
