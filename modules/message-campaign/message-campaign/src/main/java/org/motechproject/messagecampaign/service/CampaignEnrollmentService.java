package org.motechproject.messagecampaign.service;

import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.search.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignEnrollmentService {

    @Autowired
    private AllCampaignEnrollments allCampaignEnrollments;

    public void register(CampaignEnrollment enrollment) {
        allCampaignEnrollments.saveOrUpdate(enrollment);
    }

    public void unregister(String externalId, String campaignName) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        enrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
        allCampaignEnrollments.saveOrUpdate(enrollment);
    }

    public void unregister(CampaignEnrollment enrollment) {
        enrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
        allCampaignEnrollments.saveOrUpdate(enrollment);
    }

    public List<CampaignEnrollment> search(CampaignEnrollmentsQuery query) {
        List<CampaignEnrollment> enrollments = new ArrayList<CampaignEnrollment>();
        Criterion primaryCriterion = query.getPrimaryCriterion();
        if (primaryCriterion != null) {
            enrollments = primaryCriterion.fetch(allCampaignEnrollments);
        }
        for (Criterion criterion : query.getSecondaryCriteria()) {
            enrollments = criterion.filter(enrollments);
        }
        return enrollments;
    }
}
