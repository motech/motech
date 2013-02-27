package org.motechproject.server.messagecampaign.service;

import org.joda.time.DateTime;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.CampaignNotFoundException;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerFactory;
import org.motechproject.server.messagecampaign.scheduler.CampaignSchedulerService;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.server.messagecampaign.web.ex.EnrollmentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("messageCampaignService")
public class MessageCampaignServiceImpl implements MessageCampaignService {

    private CampaignEnrollmentService campaignEnrollmentService;
    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;
    private AllCampaignEnrollments allCampaignEnrollments;
    private CampaignSchedulerFactory campaignSchedulerFactory;
    private AllMessageCampaigns allMessageCampaigns;

    @Autowired
    public MessageCampaignServiceImpl(CampaignEnrollmentService campaignEnrollmentService, CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper, AllCampaignEnrollments allCampaignEnrollments, CampaignSchedulerFactory campaignSchedulerFactory,
                                      AllMessageCampaigns allMessageCampaigns) {
        this.campaignEnrollmentService = campaignEnrollmentService;
        this.campaignEnrollmentRecordMapper = campaignEnrollmentRecordMapper;
        this.allCampaignEnrollments = allCampaignEnrollments;
        this.campaignSchedulerFactory = campaignSchedulerFactory;
        this.allMessageCampaigns = allMessageCampaigns;
    }

    public void startFor(CampaignRequest request) {
        CampaignEnrollment enrollment = new CampaignEnrollment(request.externalId(), request.campaignName()).setReferenceDate(request.referenceDate()).setReferenceTime(request.referenceTime()).setDeliverTime(request.deliverTime());
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
    public Map<String, List<DateTime>> getCampaignTimings(String externalId, String campaignName, DateTime startDate, DateTime endDate) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        if (!enrollment.isActive()) {
            return new HashMap<>();
        }
        return campaignSchedulerFactory.getCampaignScheduler(campaignName).getCampaignTimings(startDate, endDate, enrollment);
    }

    @Override
    public void updateEnrollment(CampaignRequest enrollRequest, String enrollmentId) {
        CampaignEnrollment existingEnrollment = allCampaignEnrollments.get(enrollmentId);

        if (existingEnrollment == null) {
            throw new EnrollmentNotFoundException("Enrollment with id " + enrollmentId + " not found");
        } else {
            CampaignEnrollment byIdAndName = allCampaignEnrollments.findByExternalIdAndCampaignName(
                    enrollRequest.externalId(), enrollRequest.campaignName());
            if (byIdAndName != null && !byIdAndName.getId().equals(enrollmentId)) {
                throw new IllegalArgumentException(String.format("%s is already enrolled in %s campaign, enrollmentId: %s",
                        enrollRequest.externalId(), enrollRequest.campaignName(), byIdAndName.getId()));
            }
        }

        campaignSchedulerFactory.getCampaignScheduler(existingEnrollment.getCampaignName()).stop(existingEnrollment);

        existingEnrollment.setExternalId(enrollRequest.externalId()).setDeliverTime(enrollRequest.deliverTime())
                .setReferenceDate(enrollRequest.referenceDate()).setReferenceTime(enrollRequest.referenceTime());
        allCampaignEnrollments.saveOrUpdate(existingEnrollment);

        campaignSchedulerFactory.getCampaignScheduler(existingEnrollment.getCampaignName()).start(existingEnrollment);
    }

    @Override
    public void stopAll(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollment> enrollments = campaignEnrollmentService.search(query);
        for (CampaignEnrollment enrollment : enrollments) {
            campaignEnrollmentService.unregister(enrollment.getExternalId(), enrollment.getCampaignName());
            campaignSchedulerFactory.getCampaignScheduler(enrollment.getCampaignName()).stop(enrollment);
        }
    }

    @Override
    public void saveCampaign(CampaignRecord campaign) {
        allMessageCampaigns.saveOrUpdate(campaign);
    }

    @Override
    public void deleteCampaign(String campaignName) {
        CampaignRecord campaignRecord = allMessageCampaigns.findFirstByName(campaignName);

        if (campaignRecord == null) {
            throw new CampaignNotFoundException("Campaign not found: " + campaignName);
        } else {
            CampaignEnrollmentsQuery enrollmentsQuery = new CampaignEnrollmentsQuery().withCampaignName(campaignName);
            stopAll(enrollmentsQuery);

            allMessageCampaigns.remove(campaignRecord);
        }
    }

    @Override
    public CampaignRecord getCampaignRecord(String campaignName) {
        return allMessageCampaigns.findFirstByName(campaignName);
    }

    @Override
    public List<CampaignRecord> getAllCampaignRecords() {
        return allMessageCampaigns.getAll();
    }
}
