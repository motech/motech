package org.motechproject.mds.domain;

import java.util.ArrayList;

/**
 * The <code>ImportExportBlueprint</code> represents MDS import or export plan, specifying which entities
 * and which parts of those entities should be included in either import or export.
 *
 * @see org.motechproject.mds.service.ImportExportService
 */
public class ImportExportBlueprint extends ArrayList<ImportExportBlueprint.Record> {

    public void includeEntitySchema(String entityName, boolean includeSchema) {
        ensureRecord(entityName).setIncludeSchema(includeSchema);
    }

    public void includeEntityData(String entityName, boolean includeData) {
        ensureRecord(entityName).setIncludeData(includeData);
    }

    public boolean isIncludeEntitySchema(String entityName) {
        Record record = getRecord(entityName);
        return null != record && record.isIncludeSchema();
    }

    public boolean isIncludeEntityData(String entityName) {
        Record record = getRecord(entityName);
        return null != record && record.isIncludeData();
    }

    private Record ensureRecord(String entityName) {
        Record record = getRecord(entityName);
        if (null == record) {
            record = new Record();
            record.setEntityName(entityName);
            add(record);
            return record;
        }
        return record;
    }

    private Record getRecord(String entityName) {
        for (Record record : this) {
            if (record.getEntityName().equals(entityName)) {
                return record;
            }
        }
        return null;
    }

    public static class Record {

        private String entityName;
        private boolean includeSchema;
        private boolean includeData;

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public boolean isIncludeSchema() {
            return includeSchema;
        }

        public void setIncludeSchema(boolean includeSchema) {
            this.includeSchema = includeSchema;
        }

        public boolean isIncludeData() {
            return includeData;
        }

        public void setIncludeData(boolean includeData) {
            this.includeData = includeData;
        }
    }
}
