package org.motechproject.mds.repository;

import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.EntityMapping;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The <code>AllEntityMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.EntityMapping}.
 */
@Repository
public class AllEntityMappings extends BaseMdsRepository {

    @Transactional
    public EntityMapping save(String className) {
        EntityMapping mapping = new EntityMapping();
        mapping.setClassName(className);

        return getPersistenceManager().makePersistent(mapping);
    }

    @Transactional
    public boolean containsEntity(String simpleName) {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("className == name");
        query.declareParameters("java.lang.String name");

        String className = String.format("%s.%s", EntityBuilder.PACKAGE, simpleName);
        Collection collection = (Collection) query.execute(className);
        List<EntityMapping> mappings = cast(EntityMapping.class, collection);

        return !mappings.isEmpty();
    }

    @Transactional
    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(EntityMapping.class);
        query.setFilter("entityId == id");
        query.declareParameters("java.lang.Long entityId");
        query.setUnique(true);

        EntityMapping entityMapping = (EntityMapping) query.execute(id);

        if (entityMapping != null) {
            if (isNotBlank(entityMapping.getModule()) || isNotBlank(entityMapping.getNamespace())) {
                throw new EntityReadOnlyException();
            }

            getPersistenceManager().deletePersistent(entityMapping);
        } else {
            throw new EntityNotFoundException();
        }
    }
}
