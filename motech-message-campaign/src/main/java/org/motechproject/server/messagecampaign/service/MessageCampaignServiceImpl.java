package org.motechproject.server.messagecampaign.service;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.MessageCampaignException;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.scheduler.MessageCampaignScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MessageCampaignServiceImpl implements MessageCampaignService {
    private MotechSchedulerService schedulerService;
    private AllMessageCampaigns allMessageCampaigns;
    private CampaignEnrollmentService campaignEnrollmentService;
    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;

    @Autowired
    public MessageCampaignServiceImpl(AllMessageCampaigns allMessageCampaigns, MotechSchedulerService schedulerService,
                                      CampaignEnrollmentService campaignEnrollmentService, CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper) {
        this.allMessageCampaigns = allMessageCampaigns;
        this.schedulerService = schedulerService;
        this.campaignEnrollmentService = campaignEnrollmentService;
        this.campaignEnrollmentRecordMapper = campaignEnrollmentRecordMapper;
    }

    public void startFor(CampaignRequest request) {
        getScheduler(request).start();
    }

    //TODO should remove this method
    public void stopFor(CampaignRequest request, String message) {
        getScheduler(request).stop(message);
    }

    @Override
    public void stopAll(CampaignRequest enrollRequest) {
        MessageCampaignScheduler scheduler = getScheduler(enrollRequest);
        scheduler.stop();
    }

    @Override
    public List<CampaignEnrollmentRecord> search(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<CampaignEnrollmentRecord>();
        for (CampaignEnrollment campaignEnrollment : campaignEnrollmentService.search(query))
            campaignEnrollmentRecords.add(campaignEnrollmentRecordMapper.map(campaignEnrollment));
        return campaignEnrollmentRecords;
    }

    @Override
    public Map<String, List<Date>> getCampaignTimings(String externalId, String campaignName, Date startDate, Date endDate) {
        MessageCampaignScheduler messageCampaignScheduler =
                getScheduler(new CampaignRequest(externalId, campaignName, null, null));

        return messageCampaignScheduler.getCampaignTimings(startDate, endDate);
    }

    private MessageCampaignScheduler getScheduler(CampaignRequest enrollRequest) {
        Campaign<CampaignMessage> campaign = allMessageCampaigns.get(enrollRequest.campaignName());
        if (campaign == null)
            throw new MessageCampaignException("No campaign by name : " + enrollRequest.campaignName());
        return campaign.getScheduler(schedulerService, campaignEnrollmentService, enrollRequest);
    }
}
