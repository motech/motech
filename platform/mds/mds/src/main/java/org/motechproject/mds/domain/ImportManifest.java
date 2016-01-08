package org.motechproject.mds.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>ImportManifest</code> holds components available for import that are contained in
 * a single MDS import file.
 *
 * @see org.motechproject.mds.service.ImportExportService
 */
public class ImportManifest {
    private String importId;
    private List<Record> records;

    public ImportManifest() {
        this.records = new ArrayList<>();
    }

    public Record addRecord(String entityName, String moduleName) {
        Record record = new Record();
        record.setEntityName(entityName);
        record.setModuleName(moduleName);
        records.add(record);
        return record;
    }

    public String getImportId() {
        return importId;
    }

    public void setImportId(String importId) {
        this.importId = importId;
    }

    public List<Record> getRecords() {
        return records;
    }

    public static class Record {
        private String entityName;
        private String moduleName;
        private boolean canIncludeSchema;
        private boolean canIncludeData;

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public boolean isCanIncludeSchema() {
            return canIncludeSchema;
        }

        public void setCanIncludeSchema(boolean canIncludeSchema) {
            this.canIncludeSchema = canIncludeSchema;
        }

        public boolean isCanIncludeData() {
            return canIncludeData;
        }

        public void setCanIncludeData(boolean canIncludeData) {
            this.canIncludeData = canIncludeData;
        }
    }
}
