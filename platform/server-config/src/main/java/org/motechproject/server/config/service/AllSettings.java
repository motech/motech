package org.motechproject.server.config.service;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.server.config.domain.SettingsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSettings extends MotechBaseRepository<SettingsRecord> {

    @Autowired
    public AllSettings(@Qualifier("settingsDbConnector") CouchDbConnector couchDbConnector) {
        super(SettingsRecord.class, couchDbConnector);
    }

    public SettingsRecord getSettings() {
        List<SettingsRecord> settingsRecordList = getAll();
        return (settingsRecordList == null || settingsRecordList.isEmpty()) ? new SettingsRecord() :
                settingsRecordList.get(0);
    }

    public void addOrUpdateSettings(SettingsRecord settingsRecord) {
        if (settingsRecord.getId() == null) {
            add(settingsRecord);
        } else {
            update(settingsRecord);
        }
    }
}
