package org.motechproject.mds.repository;


import org.motechproject.mds.domain.AvailableFieldType;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.domain.ValidationCriterion;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>AllTypeValidationMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.TypeValidation}.
 */
@Repository
public class AllTypeValidationMappings extends BaseMdsRepository {

    public TypeValidation save(AvailableFieldType type) {
        TypeValidation typeValidation = new TypeValidation(type);
        return save(typeValidation);
    }

    public TypeValidation save(TypeValidation typeValidation) {
        return getPersistenceManager().makePersistent(typeValidation);
    }

    public TypeValidation getValidationForType(AvailableFieldType type) {
        Query query = getPersistenceManager().newQuery(TypeValidation.class);
        query.setFilter("typeId == type");
        query.declareParameters("java.lang.Long typeId");
        query.setUnique(true);

        return (TypeValidation) query.execute(type.getId());
    }

    public TypeValidation createValidationInstance(AvailableFieldType type) {
        TypeValidation emptyValidation = null;
        TypeValidation typeValidation = getValidationForType(type);

        if (typeValidation != null) {
            emptyValidation = new TypeValidation();
            emptyValidation.setName(type.getDefaultName());
            List<ValidationCriterion> emptyCriteria = new ArrayList<>();
            for (ValidationCriterion criterionMapping : typeValidation.getCriteria()) {
                emptyCriteria.add(new ValidationCriterion(criterionMapping.getDisplayName(), emptyValidation, criterionMapping.getType()));
            }

            emptyValidation.setCriteria(emptyCriteria);
        }

        return emptyValidation;
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(TypeValidation.class);
        query.setFilter("typeValidationId == id");
        query.declareParameters("java.lang.Long typeValidationId");
        query.setUnique(true);

        TypeValidation result = (TypeValidation) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }
}
