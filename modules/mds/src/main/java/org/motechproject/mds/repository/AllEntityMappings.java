package org.motechproject.mds.repository;

import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * The <code>AllEntityMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.EntityMapping}.
 */
@Repository
public class AllEntityMappings extends BaseMdsRepository {

    public EntityMapping save(EntityDto entity) {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(entity.getClassName());
        mapping.setName(entity.getName());
        mapping.setModule(entity.getModule());
        mapping.setNamespace(entity.getNamespace());

        return getPersistenceManager().makePersistent(mapping);
    }

    public boolean containsEntity(String className) {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");

        Collection collection = (Collection) query.execute(className);
        List<EntityMapping> mappings = cast(EntityMapping.class, collection);

        return !mappings.isEmpty();
    }

    public void delete(Long id) {
        EntityMapping entityMapping = getEntityById(id);

        if (entityMapping != null) {
            if (entityMapping.isReadOnly()) {
                throw new EntityReadOnlyException();
            }

            delete(entityMapping);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public void delete(EntityMapping entity) {
        getPersistenceManager().deletePersistent(entity);
    }

    public EntityMapping getEntityById(Long id) {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("entityId == id");
        query.declareParameters("java.lang.Long entityId");
        query.setUnique(true);

        return (EntityMapping) query.execute(id);
    }

    public List<EntityMapping> getAllEntities() {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        Collection collection = (Collection) query.execute("*");
        return cast(EntityMapping.class, collection);
    }
}
