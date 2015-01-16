package org.motechproject.mds.domain;

import java.util.ArrayList;

/**
 * The <code>ImportExportBlueprint</code> represents MDS import or export plan, specifying which entities
 * and which parts of those entities should be included in either import or export.
 *
 * @see org.motechproject.mds.service.ImportExportService
 */
public class ImportExportBlueprint extends ArrayList<ImportExportBlueprint.Record> {

    public void includeEntitySchema(String entityName) {
        ensureEntity(entityName).setIncludeSchema(true);
    }

    public void includeEntityData(String entityName) {
        ensureEntity(entityName).setIncludeData(true);
    }

    private Record ensureEntity(String entityName) {
        for (Record record : this) {
            if (record.getEntityName().equals(entityName)) {
                return record;
            }
        }
        Record record = new Record();
        record.setEntityName(entityName);
        add(record);
        return record;
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
