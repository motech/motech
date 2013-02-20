package org.motechproject.server.messagecampaign.dao;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMessageCampaigns extends MotechBaseRepository<CampaignRecord> {

    @Autowired
    public AllMessageCampaigns(@Qualifier("messageCampaignDBConnector") CouchDbConnector db) {
        super(CampaignRecord.class, db);
    }

    public Campaign getCampaign(String campaignName) {
        CampaignRecord record = findFirstByName(campaignName);
        return record == null ? null : record.build();
    }

    public CampaignMessage getMessage(String campaignName, String messageKey) {
        Campaign campaign = getCampaign(campaignName);
        if (campaign != null) {
            for (Object message : campaign.getMessages()) {
                CampaignMessage campaignMessage = (CampaignMessage) message;
                if (campaignMessage.messageKey().equals(messageKey)) {
                    return campaignMessage;
                }
            }
        }
        return null;
    }

    public void saveOrUpdate(CampaignRecord campaignRecord) {
        CampaignRecord existingRecord = findFirstByName(campaignRecord.getName());

        if (existingRecord == null) {
            add(campaignRecord);
        } else {
            existingRecord.updateFrom(campaignRecord);
            update(existingRecord);
        }
    }

    @View(name = "by_name", map = "function(doc) { if(doc.type === 'CampaignRecord') emit(doc.name); }")
    public List<CampaignRecord> findByName(String campaignName) {
        return queryView("by_name", campaignName);
    }

    public CampaignRecord findFirstByName(String campaignName) {
        List<CampaignRecord> records = findByName(campaignName);
        return CollectionUtils.isEmpty(records) ? null : records.get(0);
    }
}
