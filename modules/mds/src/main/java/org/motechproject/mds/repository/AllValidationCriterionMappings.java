package org.motechproject.mds.repository;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.domain.ValidationCriterionMapping;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.springframework.stereotype.Repository;

import javax.jdo.Query;

/**
 * The <code>AllValidationCriterionMappings</code> class is a repository class that operates on instances of
 * {@link org.motechproject.mds.domain.ValidationCriterionMapping}.
 */
@Repository
public class AllValidationCriterionMappings extends BaseMdsRepository {

    public ValidationCriterionMapping save(ValidationCriterionDto criterion, TypeValidationMapping validation, AvailableFieldTypeMapping type) {
        ValidationCriterionMapping validationCriterionMapping;
        if (criterion.getValue() != null) {
            validationCriterionMapping = new ValidationCriterionMapping(criterion.getDisplayName(), criterion.getValue().toString(), criterion.isEnabled(), validation, type);
        } else {
            validationCriterionMapping = new ValidationCriterionMapping(criterion.getDisplayName(), null, criterion.isEnabled(), validation, type);
        }

        return getPersistenceManager().makePersistent(validationCriterionMapping);
    }

    public void delete(Long id) {
        Query query = getPersistenceManager().newQuery(ValidationCriterionMapping.class);
        query.setFilter("criterionId == id");
        query.declareParameters("java.lang.Long criterionId");
        query.setUnique(true);

        ValidationCriterionMapping result = (ValidationCriterionMapping) query.execute(id);

        if (result != null) {
            getPersistenceManager().deletePersistent(result);
        }
    }
}
