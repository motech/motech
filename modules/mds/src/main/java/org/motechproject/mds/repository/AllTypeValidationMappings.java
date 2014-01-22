package org.motechproject.mds.repository;


import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;

/**
 * The <code>AllTypeValidationMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeValidationMapping}.
 */
@Repository
public class AllTypeValidationMappings extends BaseMdsRepository {

    public TypeValidationMapping save(AvailableFieldTypeMapping type) {
        TypeValidationMapping typeValidationMapping = new TypeValidationMapping(type);

        return getPersistenceManager().makePersistent(typeValidationMapping);
    }

    public TypeValidationMapping getValidationForType(AvailableFieldTypeMapping type) {
        Query query = getPersistenceManager().newQuery(TypeValidationMapping.class);
        query.setFilter("typeId == type");
        query.declareParameters("java.lang.Long typeId");
        query.setUnique(true);

        return (TypeValidationMapping) query.execute(type.getId());
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(TypeValidationMapping.class);
        query.setFilter("typeValidationId == id");
        query.declareParameters("java.lang.Long typeValidationId");
        query.setUnique(true);

        TypeValidationMapping result = (TypeValidationMapping) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }
}
