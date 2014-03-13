package org.motechproject.mds.repository;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.EntityAudit;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.springframework.stereotype.Repository;

/**
 * This a repository for persisting entity audits. It provides methods which created audits, by cloning the giving
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
            audit.setRestOptions(entity.getRestOptions().copy());
        }

        if (entity.getTracking() != null) {
            audit.setTracking(entity.getTracking().copy());
        }

        return create(audit);
    }
}
