package org.motechproject.mds.repository;

import org.motechproject.mds.domain.ConfigSettings;
import org.springframework.stereotype.Repository;

/**
 * <code>AllConfigSettings</code> is responsible for communication
 * with database for MDS configuration.
 */
@Repository
public class AllConfigSettings extends MotechDataRepository<ConfigSettings> {

    public AllConfigSettings() {
        super(ConfigSettings.class);
    }

    public void addOrUpdate(ConfigSettings record) {
        ConfigSettings rec = retrieve("id", 1);

        if (rec == null) {
            record.setId((long) 1);
            create(record);
        } else {
            rec.setAfterTimeValue(record.getAfterTimeValue());
            rec.setAfterTimeUnit(record.getAfterTimeUnit());
            rec.setDeleteMode(record.getDeleteMode());
            rec.setEmptyTrash(record.getEmptyTrash());
            update(rec);
        }
    }

}
