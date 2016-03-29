package org.motechproject.mds.repository.internal;

import org.motechproject.mds.domain.ConfigSettings;
import org.motechproject.mds.repository.MotechDataRepository;
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
            rec.setDefaultGridSize(record.getDefaultGridSize());
            update(rec);
        }
    }

}
