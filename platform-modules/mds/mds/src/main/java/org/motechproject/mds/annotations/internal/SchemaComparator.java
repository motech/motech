package org.motechproject.mds.annotations.internal;

import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;

/**
 * The <code>SchemaComparator</code> provides methods that checks for entity schema difference. It is used
 * during annotations processing to verify if the entity schema has actually changed between bundle deployments.
 *
 * @see org.motechproject.mds.osgi.MdsBundleWatcher
 */
@Component
public class SchemaComparator {

    private EntityService entityService;

    /**
     * Returns true if lookups are different, false otherwise.
     * It compares only read-only lookups, that created
     * in the code by developer.
     *
     * @param entityId the id of entity
     * @param newLookups the newLookups defined in the code
     * @return true if lookups are different, false otherwise.
     */
    public boolean lookupsDiffer(Long entityId, List<LookupDto> newLookups) {
        List<LookupDto> existingLookups = entityService.getEntityLookups(entityId);
        Map<String, LookupDto> entityLookupsMappings = new HashMap<>();

        for (LookupDto entityLookup : existingLookups) {
            if (entityLookup.isReadOnly()) {
                entityLookupsMappings.put(entityLookup.getLookupName(), entityLookup);
            }
        }

        if (entityLookupsMappings.size() != newLookups.size()) {
            return true;
        }

        for (LookupDto lookup : newLookups) {
            if (!lookupEquals(lookup, entityLookupsMappings.get(lookup.getLookupName()))) {
                return true;
            }
        }

        return false;
    }

    private boolean lookupEquals(LookupDto a, LookupDto b) {
        return a != null && b != null && //NO CHECKSTYLE Boolean expression complexity
                Objects.equals(a.getLookupName(), b.getLookupName()) &&
                Objects.equals(a.isSingleObjectReturn(), b.isSingleObjectReturn()) &&
                Objects.equals(a.isExposedViaRest(), b.isExposedViaRest()) &&
                Objects.equals(a.isReadOnly(), b.isReadOnly()) &&
                Objects.equals(a.getMethodName(), b.getMethodName()) &&
                Objects.equals(a.isReferenced(), b.isReferenced()) &&
                lookupFieldsEqual(a.getLookupFields(), b.getLookupFields());
    }

    private boolean lookupFieldsEqual(List<LookupFieldDto> a, List<LookupFieldDto> b) {
        ListIterator<LookupFieldDto> aIterator = a.listIterator();
        ListIterator<LookupFieldDto> bIterator = b.listIterator();
        while (aIterator.hasNext() && bIterator.hasNext()) {
            if (!lookupFieldEquals(aIterator.next(), bIterator.next())) {
                return false;
            }
        }
        return !(aIterator.hasNext() || bIterator.hasNext());
    }

    private boolean lookupFieldEquals(LookupFieldDto a, LookupFieldDto b) {
        return Objects.equals(a.getName(), b.getName()) && //NO CHECKSTYLE Boolean expression complexity
                Objects.equals(a.getRelatedName(), b.getRelatedName()) &&
                Objects.equals(a.getType(), b.getType()) &&
                Objects.equals(a.getCustomOperator(), b.getCustomOperator()) &&
                Objects.equals(a.isUseGenericParam(), b.isUseGenericParam()) &&
                Objects.equals(a.getClassName(), b.getClassName()) &&
                Objects.equals(a.getDisplayName(), b.getDisplayName()) &&
                Objects.equals(a.getSettings(), b.getSettings());
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }
}
