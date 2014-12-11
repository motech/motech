package org.motechproject.mds.validation;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.ex.FieldUsedInLookupException;
import org.motechproject.mds.ex.LookupReferencedException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ServiceUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.motechproject.mds.repository.query.DataSourceReferenceQueryExecutionHelper.DATA_SOURCE_CLASS_NAME;
import static org.motechproject.mds.repository.query.DataSourceReferenceQueryExecutionHelper.createLookupReferenceQuery;

/**
 * The <code>EntityValidator</code> class provides validation methods for entities
 */
public class EntityValidator {
    private static final String INDEXES = "indexes";

    private BundleContext bundleContext;

    public void validateEntity(EntityDraft draft) {
        validateEntityLookupsReferences(draft);
        validateEntityLookupsFieldsReferences(draft);
    }

    public void validateAdvancedSettingsEdit(Entity entity, String path) {
        if (path.length() != 0) {
            String[] splitPath = path.split("\\.");
            if (INDEXES.equals(splitPath[0])) {
                Lookup lookup = entity.getLookups().get(Integer.parseInt(splitPath[1]));
                validateLookupsReferences(Arrays.asList(lookup.getLookupName()), entity.getName());
            }
        }
    }

    public void validateFieldNotUsedByLookups(Entity entity, Long fieldId) {
        StringBuilder lookups = new StringBuilder();

        // collect the used lookup names
        for (Lookup lookup : entity.getLookups()) {
            for (Field field : lookup.getFields()) {
                if (Objects.equals(fieldId, field.getId())) {
                    if (lookups.length() != 0) {
                        lookups.append(' ');
                    }
                    lookups.append(lookup.getLookupName());
                }
            }
        }

        if (lookups.length() > 0) {
            throw new FieldUsedInLookupException(entity.getField(fieldId).getDisplayName(), lookups.toString());
        }
    }

    private void validateEntityLookupsFieldsReferences(EntityDraft draft) {
        Map<String, Lookup> draftLookups = new HashMap<>();
        List<String> validateLookups = new ArrayList<>();

        for (Lookup lookup : draft.getLookups()) {
            draftLookups.put(lookup.getLookupName(), lookup);
        }

        for (Lookup lookup : draft.getParentEntity().getLookups()) {
            Lookup draftLookup = draftLookups.get(lookup.getLookupName());
            if (draftLookup == null || lookupFieldsChanged(lookup, draftLookup)) {
                validateLookups.add(lookup.getLookupName());
            }
        }

        validateLookupsReferences(validateLookups, draft.getClassName());
    }

    private boolean lookupFieldsChanged(Lookup parentLookup, Lookup draftLookup) {
        if (parentLookup.getFields().size() != draftLookup.getFields().size()) {
            return true;
        } else {
            Map<String, Field> draftLookupFields = new HashMap<>();
            for (Field field : draftLookup.getFields()) {
                draftLookupFields.put(field.getName(), field);
            }

            for (Field field : parentLookup.getFields()) {
                Field draftField = draftLookupFields.get(field.getName());

                if (draftField == null || !Objects.equals(field.getName(), draftField.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void validateEntityLookupsReferences(EntityDraft draft) {
        List<String> parentLookupsNames = new ArrayList<>();
        List<String> draftLookupNames = new ArrayList<>();

        for (Lookup lookup : draft.getParentEntity().getLookups()) {
            parentLookupsNames.add(lookup.getLookupName());
        }

        for (Lookup lookup : draft.getLookups()) {
            draftLookupNames.add(lookup.getLookupName());
        }

        // lets find all lookups names that are present in parent entity,
        // but are not in draft entity (lookup removed or renamed)

        Set<String> validateLookups = new HashSet<>();
        validateLookups.addAll(parentLookupsNames);
        validateLookups.removeAll(draftLookupNames);

        validateLookupsReferences(validateLookups, draft.getClassName());
    }

    private void validateLookupsReferences(Collection<String> lookupsNames, String entityClassName) {
        MotechDataService dataSourceDataService = ServiceUtil.
                getServiceForInterfaceName(bundleContext, MotechClassPool.getInterfaceName(DATA_SOURCE_CLASS_NAME));
        if (dataSourceDataService != null) {
            StringBuilder lookups = new StringBuilder();
            for (String lookupName : lookupsNames) {
                long count = (long) dataSourceDataService.executeQuery(createLookupReferenceQuery(lookupName, entityClassName));
                if (count > 0) {
                    if (lookups.length() != 0) {
                        lookups.append(' ');
                    }
                    lookups.append(lookupName);
                }
            }
            if (lookups.length() > 0) {
                throw new LookupReferencedException(lookups.toString());
            }
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
