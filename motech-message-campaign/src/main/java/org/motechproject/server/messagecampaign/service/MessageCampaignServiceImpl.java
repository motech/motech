package org.motechproject.server.messagecampaign.service;

import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerFactory;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("messageCampaignService")
public class MessageCampaignServiceImpl implements MessageCampaignService {

    private CampaignEnrollmentService campaignEnrollmentService;
    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;
    private AllCampaignEnrollments allCampaignEnrollments;
    private CampaignSchedulerFactory campaignSchedulerFactory;

    @Autowired
    public MessageCampaignServiceImpl(CampaignEnrollmentService campaignEnrollmentService, CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper, AllCampaignEnrollments allCampaignEnrollments, CampaignSchedulerFactory campaignSchedulerFactory) {
        this.campaignEnrollmentService = campaignEnrollmentService;
        this.campaignEnrollmentRecordMapper = campaignEnrollmentRecordMapper;
        this.allCampaignEnrollments = allCampaignEnrollments;
        this.campaignSchedulerFactory = campaignSchedulerFactory;
    }

    public void startFor(CampaignRequest request) {
        CampaignEnrollment enrollment = new CampaignEnrollment(request.externalId(), request.campaignName()).setReferenceDate(request.referenceDate()).setDeliverTime(request.deliverTime());
        campaignEnrollmentService.register(enrollment);
        CampaignSchedulerService campaignScheduler = campaignSchedulerFactory.getCampaignScheduler(request.campaignName());
        campaignScheduler.start(enrollment);
    }

    @Override
    public void stopAll(CampaignRequest request) {
        campaignEnrollmentService.unregister(request.externalId(), request.campaignName());
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(request.externalId(), request.campaignName());
        campaignSchedulerFactory.getCampaignScheduler(request.campaignName()).stop(enrollment);
    }

    @Override
    public List<CampaignEnrollmentRecord> search(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollmentRecord> campaignEnrollmentRecords = new ArrayList<>();
        for (CampaignEnrollment campaignEnrollment : campaignEnrollmentService.search(query)) {
            campaignEnrollmentRecords.add(campaignEnrollmentRecordMapper.map(campaignEnrollment));
        }
        return campaignEnrollmentRecords;
    }

    @Override
    public Map<String, List<Date>> getCampaignTimings(String externalId, String campaignName, Date startDate, Date endDate) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        return campaignSchedulerFactory.getCampaignScheduler(campaignName).getCampaignTimings(startDate, endDate, enrollment);
    }
}
