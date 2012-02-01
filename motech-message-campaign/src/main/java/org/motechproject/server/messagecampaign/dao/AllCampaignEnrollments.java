package org.motechproject.server.messagecampaign.dao;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllCampaignEnrollments extends MotechBaseRepository<CampaignEnrollment> {

    @Autowired
    protected AllCampaignEnrollments(@Qualifier("messageCampaignDBConnector") CouchDbConnector db) {
        super(CampaignEnrollment.class, db);
    }

    @View(name = "find_by_externalId_and_campaign", map = "function(doc) {{emit([doc.externalId, doc.campaignName]);}}")
    public CampaignEnrollment findByExternalIdAndCampaignName(String externalId, String campaignName) {
        List<CampaignEnrollment> enrollments = queryView("find_by_externalId_and_campaign", ComplexKey.of(externalId, campaignName));
        return enrollments.isEmpty() ? null : enrollments.get(0);
    }

    public void saveOrUpdate(CampaignEnrollment enrollment) {
        CampaignEnrollment existingEnrollment = findByExternalIdAndCampaignName(enrollment.getExternalId(), enrollment.getCampaignName());
        if (existingEnrollment != null) {
            update(existingEnrollment.copyFrom(enrollment));
        } else {
            add(enrollment);
        }
    }
}
