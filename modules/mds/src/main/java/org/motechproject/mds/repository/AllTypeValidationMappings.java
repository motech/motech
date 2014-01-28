package org.motechproject.mds.repository;


import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.HashSet;
import java.util.Set;

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

    public TypeValidationMapping getEmptyValidationForType(AvailableFieldTypeMapping type) {
        TypeValidationMapping emptyValidation = null;
        TypeValidationMapping typeValidationMapping = getValidationForType(type);

        if (typeValidationMapping != null) {
            emptyValidation = new TypeValidationMapping();
            emptyValidation.setName(type.getDefaultName());
            Set<ValidationCriterionMapping> emptyCriteria = new HashSet<>();
            for (ValidationCriterionMapping criterionMapping : typeValidationMapping.getCriteria()) {
                emptyCriteria.add(new ValidationCriterionMapping(criterionMapping.getDisplayName(), emptyValidation, criterionMapping.getType()));
            }

            emptyValidation.setCriteria(emptyCriteria);
        }

        return emptyValidation;
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
