package org.motechproject.mds.repository;


import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AllTypeValidationMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeValidationMapping}.
 */
@Repository
public class AllTypeValidationMappings extends BaseMdsRepository {

    public TypeValidationMapping save(AvailableFieldTypeMapping type) {
        TypeValidationMapping typeValidation = new TypeValidationMapping(type);
        return save(typeValidation);
    }

    public TypeValidationMapping save(TypeValidationMapping typeValidation) {
        return getPersistenceManager().makePersistent(typeValidation);
    }

    public TypeValidationMapping getValidationForType(AvailableFieldTypeMapping type) {
        Query query = getPersistenceManager().newQuery(TypeValidationMapping.class);
        query.setFilter("typeId == type");
        query.declareParameters("java.lang.Long typeId");
        query.setUnique(true);

        return (TypeValidationMapping) query.execute(type.getId());
    }

    public TypeValidationMapping createValidationInstance(AvailableFieldTypeMapping type) {
        TypeValidationMapping emptyValidation = null;
        TypeValidationMapping typeValidationMapping = getValidationForType(type);

        if (typeValidationMapping != null) {
            emptyValidation = new TypeValidationMapping();
            emptyValidation.setName(type.getDefaultName());
            List<ValidationCriterionMapping> emptyCriteria = new ArrayList<>();
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
