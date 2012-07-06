package org.motechproject.server.startup.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.startup.domain.SettingsRecord;

import java.util.ArrayList;
import java.util.List;

public class AllSettings extends MotechBaseRepository<SettingsRecord> {

    public AllSettings(CouchDbConnector couchDbConnector) {
        super(SettingsRecord.class, couchDbConnector);
    }

    public SettingsRecord getSettings() {
        List<SettingsRecord> settingsRecordList = new ArrayList<>();
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
