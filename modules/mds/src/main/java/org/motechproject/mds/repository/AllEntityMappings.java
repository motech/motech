package org.motechproject.mds.repository;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.springframework.stereotype.Repository;

import javax.jdo.Extent;
import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

/**
 * The <code>AllEntityMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.Entity}.
 */
@Repository
public class AllEntityMappings extends BaseMdsRepository {

    public Entity save(EntityDto entity) {
        Entity mapping = new Entity();
        mapping.setClassName(entity.getClassName());
        mapping.setName(entity.getName());
        mapping.setModule(entity.getModule());
        mapping.setNamespace(entity.getNamespace());

        return getPersistenceManager().makePersistent(mapping);
    }

    public boolean containsEntity(String className) {
        Query query = getPersistenceManager().newQuery(Entity.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");

        Collection collection = (Collection) query.execute(className);
        List<Entity> mappings = cast(Entity.class, collection);

        return !mappings.isEmpty();
    }

    public void delete(Long id) {
        Entity entity = getEntityById(id);

        if (entity != null) {
            if (entity.isReadOnly()) {
                throw new EntityReadOnlyException();
            }

            delete(entity);
        } else {
            throw new EntityNotFoundException();
        }
    }

    public void delete(Entity entity) {
        getPersistenceManager().deletePersistent(entity);
    }

    public Entity getEntityById(Long id) {
        Query query = getPersistenceManager().newQuery(Entity.class);
        query.setFilter("entityId == id");
        query.declareParameters("java.lang.Long entityId");
        query.setUnique(true);

        return (Entity) query.execute(id);
    }

    public Entity getEntityByClassName(String name) {
        Extent extent = getPersistenceManager().getExtent(Entity.class, false);
        Query query = getPersistenceManager().newQuery(extent);
        query.setFilter("name == className");
        query.declareParameters("java.lang.String name");
        query.setUnique(true);

        return (Entity) query.execute(name);
    }

    public List<Entity> getAllEntities() {
        Query query = getPersistenceManager().newQuery(Entity.class);
        Collection collection = (Collection) query.execute("*");
        return cast(Entity.class, collection);
    }
}
