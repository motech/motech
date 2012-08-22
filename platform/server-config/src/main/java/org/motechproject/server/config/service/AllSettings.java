package org.motechproject.server.config.service;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.config.domain.SettingsRecord;

import java.util.List;

public class AllSettings extends MotechBaseRepository<SettingsRecord> {

    public AllSettings(CouchDbConnector couchDbConnector) {
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
