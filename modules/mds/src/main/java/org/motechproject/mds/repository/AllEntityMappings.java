package org.motechproject.mds.repository;

import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.service.EntityBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.Query;
import java.util.Collection;
import java.util.List;

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
        query.declareParameters("String name");

        String className = String.format("%s.%s", EntityBuilder.PACKAGE, simpleName);
        Collection collection = (Collection) query.execute(className);
        List<EntityMapping> mappings = cast(EntityMapping.class, collection);

        return !mappings.isEmpty();
    }
}
