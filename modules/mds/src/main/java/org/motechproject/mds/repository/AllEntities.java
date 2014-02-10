package org.motechproject.mds.repository;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.springframework.stereotype.Repository;

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
        entity.setNamespace(dto.getNamespace());

        return create(entity);
    }

    public boolean contains(String className) {
        return exists("className", className);
    }

    public void delete(Long id) {
        Entity entity = retrieveById(id);

        if (entity != null) {
            if (entity.isReadOnly()) {
                throw new EntityReadOnlyException();
            }

            delete(entity);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public Entity retrieveById(Long id) {
        return retrieve("id", id);
    }

    public Entity retrieveByClassName(String className) {
        return retrieve("className", className);
    }

}
