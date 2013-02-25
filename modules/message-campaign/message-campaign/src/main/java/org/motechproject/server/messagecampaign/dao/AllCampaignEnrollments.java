package org.motechproject.server.messagecampaign.dao;

import org.apache.commons.lang.StringUtils;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCampaignEnrollments extends MotechBaseRepository<CampaignEnrollment> {

    @Autowired
    protected AllCampaignEnrollments(@Qualifier("messageCampaignDBConnector") CouchDbConnector db) {
        super(CampaignEnrollment.class, db);
    }

    @View(name = "find_by_externalId_and_campaign", map = "function(doc) {if(doc.type === 'CampaignEnrollment') emit([doc.externalId, doc.campaignName]);}")
    public CampaignEnrollment findByExternalIdAndCampaignName(String externalId, String campaignName) {
        List<CampaignEnrollment> enrollments = queryView("find_by_externalId_and_campaign", ComplexKey.of(externalId, campaignName));
        return enrollments.isEmpty() ? null : enrollments.get(0);
    }

    public void saveOrUpdate(CampaignEnrollment enrollment) {
        CampaignEnrollment existingEnrollment;

        // find by id if given, else by externalId and campaignName
        if (StringUtils.isNotBlank(enrollment.getId())) {
            existingEnrollment = get(enrollment.getId());
        } else {
            existingEnrollment = findByExternalIdAndCampaignName(enrollment.getExternalId(), enrollment.getCampaignName());
        }

        if (existingEnrollment != null) {
            update(existingEnrollment.copyFrom(enrollment));
        } else {
            add(enrollment);
        }
    }

    @View(name = "by_status", map = "function(doc) { if(doc.type === 'CampaignEnrollment') emit(doc.status); }")
    public List<CampaignEnrollment> findByStatus(CampaignEnrollmentStatus status) {
        return queryView("by_status", status.name());
    }

    @View(name = "by_externalId", map = "function(doc) { if(doc.type === 'CampaignEnrollment') emit(doc.externalId); }")
    public List<CampaignEnrollment> findByExternalId(String externalId) {
        return queryView("by_externalId", externalId);
    }

    @View(name = "by_campaignName", map = "function(doc) { if(doc.type === 'CampaignEnrollment') emit(doc.campaignName); }")
    public List<CampaignEnrollment> findByCampaignName(String campaignName) {
        return queryView("by_campaignName", campaignName);
    }
}
