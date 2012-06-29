package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.AbsoluteCampaign;
import org.motechproject.server.messagecampaign.domain.message.AbsoluteCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;

import java.util.HashMap;

import static ch.lambdaj.Lambda.max;
import static ch.lambdaj.Lambda.on;
import static org.motechproject.util.DateUtil.newDateTime;

public class AbsoluteProgramScheduler extends MessageCampaignScheduler<AbsoluteCampaignMessage, AbsoluteCampaign> {

    private CampaignEnrollmentService campaignEnrollmentService;
    public AbsoluteProgramScheduler(MotechSchedulerService schedulerService, CampaignRequest campaignRequest, AbsoluteCampaign campaign, CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, campaignRequest, campaign, campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(AbsoluteCampaignMessage absoluteCampaignMessage) {
        HashMap<String, Object> params = jobParams(absoluteCampaignMessage.messageKey());
        scheduleJobOn(campaignRequest.reminderTime(), absoluteCampaignMessage.date(), params);
    }

    @Override
    protected DateTime getCampaignEnd() {
        LocalDate maxDate = max(campaign.messages(), on(AbsoluteCampaignMessage.class).date());
        return newDateTime(maxDate, campaignRequest.reminderTime());
    }

    @Override
    protected String getCampaignMessageSubject(AbsoluteCampaignMessage absoluteCampaignMessage) {
        return EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;
    }
}
