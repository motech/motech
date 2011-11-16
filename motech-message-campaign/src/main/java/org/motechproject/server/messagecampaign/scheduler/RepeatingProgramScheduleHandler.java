package org.motechproject.server.messagecampaign.scheduler;

import org.apache.log4j.Logger;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.apache.commons.lang.StringUtils.replace;
import static org.joda.time.Days.daysBetween;
import static org.motechproject.server.messagecampaign.EventKeys.MESSAGE_KEY;
import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class RepeatingProgramScheduleHandler {

    private OutboundEventGateway outboundEventGateway;
    private static final Logger log = Logger.getLogger(RepeatingProgramScheduleHandler.class);
    private AllMessageCampaigns allMessageCampaigns;

    public static final String OFFSET = "{Offset}";
    public static final String WEEK_DAY = "{WeekDay}";

    @Autowired
    public RepeatingProgramScheduleHandler(OutboundEventGateway outboundEventGateway, AllMessageCampaigns allMessageCampaigns) {
        this.outboundEventGateway = outboundEventGateway;
        this.allMessageCampaigns = allMessageCampaigns;
    }

    @MotechListener(subjects = {RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT})
    public void handleEvent(MotechEvent event) {
        log.info("handled internal repeating campaign event and forwarding: " + event.getParameters().hashCode());

        RepeatingCampaignMessage repeatingCampaignMessage = (RepeatingCampaignMessage) getCampaignMessage(event);

        if(!repeatingCampaignMessage.isApplicable()) return;
        int repeatIntervalInDays = repeatingCampaignMessage.repeatIntervalInDaysForOffset();
        Integer interval = (daysBetween(newDateTime(event.getStartTime()).withTimeAtStartOfDay(),
                DateUtil.now().withTimeAtStartOfDay()).getDays() / repeatIntervalInDays) + 1;

        replaceMessageKeyParams(event.getParameters(), OFFSET, interval.toString());
        replaceMessageKeyParams(event.getParameters(), WEEK_DAY, DateUtil.now().dayOfWeek().getAsText());
        outboundEventGateway.sendEventMessage(new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getParameters()));
    }

    private void replaceMessageKeyParams(Map<String, Object> parameters, String parameterName, String value) {
        parameters.put(MESSAGE_KEY, replace(parameters.get(MESSAGE_KEY).toString(), parameterName, value));
    }

    private CampaignMessage getCampaignMessage(MotechEvent motechEvent) {
        String campaignName = (String) motechEvent.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) motechEvent.getParameters().get(EventKeys.MESSAGE_KEY);
        return allMessageCampaigns.get(campaignName, messageKey);
    }
}
