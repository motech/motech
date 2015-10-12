package org.motechproject.mds.repository;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Tracking;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.entity.EntityReadOnlyException;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The <code>AllEntities</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.Entity}.
 */
@Repository
public class AllEntities extends MotechDataRepository<Entity> {

    public AllEntities() {
        super(Entity.class);
    }

    public Entity create(EntityDto dto) {
        Entity entity = new Entity();
        entity.setClassName(dto.getClassName());
        entity.setName(dto.getName());
        entity.setModule(dto.getModule());
        entity.setBundleSymbolicName(dto.getBundleSymbolicName());
        entity.setNamespace(dto.getNamespace());
        entity.setTableName(dto.getTableName());
        entity.setMaxFetchDepth(dto.getMaxFetchDepth());
        entity.setSecurityMode(dto.getSecurityMode());
        entity.setSecurityMembers(dto.getSecurityMembers());
        entity.setReadOnlySecurityMode(dto.getReadOnlySecurityMode());
        entity.setReadOnlySecurityMembers(dto.getReadOnlySecurityMembers());
        entity.setSuperClass(dto.getSuperClass());
        entity.setAbstractClass(dto.isAbstractClass());
        entity.setIsDiscriminated(dto.isDiscriminated());
        Tracking tracking = new Tracking();
        tracking.setEntity(entity);
        tracking.setRecordHistory(dto.isRecordHistory());
        entity.setTracking(tracking);

        return create(entity);
    }

    public boolean contains(String className) {
        return exists("className.toLowerCase()", className.toLowerCase());
    }

    public void delete(Long id) {
        Entity entity = retrieveById(id);

        if (entity != null) {
            if (entity.isDDE()) {
                throw new EntityReadOnlyException(entity.getName());
            }

            delete(entity);
        } else {
            throw new EntityNotFoundException(id);
        }
    }

    public Entity retrieveById(Long id) {
        return retrieve("id", id);
    }

    public Entity retrieveByClassName(String className) {
        List<Entity> entities = retrieveAll("className", className);
        for (Entity entity : entities) {
            if (entity.isActualEntity()) {
                return entity;
            }
        }
        return null;
    }

    public Entity updateAndIncrementVersion(Entity entity) {
        entity.incrementVersion();
        return super.update(entity);
    }
}
