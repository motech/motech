package org.motechproject.mds.repository;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityAudit;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.domain.Tracking;
import org.springframework.stereotype.Repository;

/**
 * This is a repository for persisting entity audits. It provides methods which create audits, by cloning the given
 * entity.
 */
@Repository
public class AllEntityAudits extends MotechDataRepository<EntityAudit> {

    public AllEntityAudits() {
        super(EntityAudit.class);
    }

    public EntityAudit createAudit(Entity entity, String username) {

        EntityAudit audit = new EntityAudit();

        audit.setOwnerUsername(username);
        audit.setModificationDate(DateUtil.nowUTC());
        audit.setVersion(entity.getEntityVersion());

        audit.setName(entity.getName());
        audit.setClassName(entity.getClassName());
        audit.setNamespace(entity.getNamespace());
        audit.setTableName(entity.getTableName());
        audit.setModule(entity.getModule());

        for (Field field : entity.getFields()) {
            Field tmp = field.copy();
            tmp.setId(null);
            audit.addField(tmp);
        }

        for (Lookup lookup : entity.getLookups()) {
            audit.addLookup(lookup.copy(entity.getFields()));
        }

        if (entity.getRestOptions() != null) {
            RestOptions restOptions = entity.getRestOptions().copy();
            restOptions.setEntity(audit);
            audit.setRestOptions(restOptions);
        }

        if (entity.getTracking() != null) {
            Tracking tracking = entity.getTracking().copy();
            tracking.setEntity(audit);
            audit.setTracking(tracking);
        }

        return create(audit);
    }
}
