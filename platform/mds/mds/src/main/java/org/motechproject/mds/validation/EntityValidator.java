package org.motechproject.mds.validation;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.exception.UserSuppliedComboboxValuesUsedException;
import org.motechproject.mds.exception.entity.IncompatibleComboboxFieldException;
import org.motechproject.mds.exception.field.FieldUsedInLookupException;
import org.motechproject.mds.exception.lookup.LookupReferencedException;
import org.motechproject.mds.helper.ClassTableName;
import org.motechproject.mds.helper.ComboboxHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static javax.jdo.Query.SQL;
import static org.motechproject.mds.repository.query.DataSourceReferenceQueryExecutionHelper.DATA_SOURCE_CLASS_NAME;
import static org.motechproject.mds.repository.query.DataSourceReferenceQueryExecutionHelper.createLookupReferenceQuery;
import static org.motechproject.mds.util.Constants.Config.MYSQL_DRIVER_CLASSNAME;

/**
 * The <code>EntityValidator</code> class provides validation methods for entities
 */
public class EntityValidator {

    private static final String INDEXES = "indexes";
    private static final String GET_COMBOBOXES_WITH_MULTIPLE_VALUES_QUERY = "SELECT FROM %s WHERE %s.size() > 1";
    private static final String SELECT_DISTINCT_QUERY = "SELECT DISTINCT %s FROM %s WHERE %s IS NOT NULL";

    private BundleContext bundleContext;
    private PersistenceManagerFactory persistenceManagerFactory;

    /**
     * Validates whether changes given in draft can be applied to parent entity.
     *
     * @param draft  the draft of the entity
     */
    public void validateEntity(EntityDraft draft) {
        validateEntityLookupsReferences(draft);
        validateEntityLookupsFieldsReferences(draft);
        validateEntityComboboxFieldsSelectionTypesChanges(draft);
        validateEntityComboboxesValues(draft);
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

    private void validateEntityComboboxFieldsSelectionTypesChanges(EntityDraft draft) {
        Entity parent = draft.getParentEntity();

        Map<String, Boolean> changedFields = ComboboxHelper.comboboxesWithChangedSelectionType(parent.getComboboxFields(), draft.getComboboxFields());

        for (Map.Entry<String, Boolean> field : changedFields.entrySet()) {
            validateField(parent, field);
        }
    }

    private void validateField(Entity parent, Map.Entry<String, Boolean> field) {
        if (!field.getValue()) {
            if (!executeJDOQLQuery(prepareGetComboboxesWithMultipleValuesQuery(parent, field.getKey())).isEmpty()) {
                throw new IncompatibleComboboxFieldException(parent.getName(), field.getKey());
            }
        }
    }

    private void validateEntityComboboxesValues(EntityDraft draft) {

        Entity parent = draft.getParentEntity();

        List<Field> oldComboboxFields = draft.getParentEntity().getStringComboboxFields();
        List<Field> comboboxFields = draft.getStringComboboxFields();

        for (Field field : substract(oldComboboxFields, comboboxFields)) {

            String tableName;
            String fieldName;

            if (!field.isMultiSelectCombobox()) {
                fieldName = field.getName();
                tableName = ClassTableName.getTableName(parent);
            } else {
                fieldName = "ELEMENT";
                tableName = ClassTableName.getTableName(parent) + "_" + field.getName().toUpperCase();
            }

            List<?> values = executeSQLQuery(prepareSelectDistincQuery(tableName, fieldName));

            validateUserSuppliedValuesUsageForField(values, draft.getField(field.getName()));
        }
    }

    private void validateUserSuppliedValuesUsageForField(List<?> values, Field field) {

        String allowedFields = field.getSettingByName(Constants.Settings.COMBOBOX_VALUES).getValue();

        for (Object value : values) {
            if (!allowedFields.contains(value.toString())) {
                throw new UserSuppliedComboboxValuesUsedException(field.getName(), value.toString());
            }
        }
    }

    private String prepareGetComboboxesWithMultipleValuesQuery(Entity entity, String field) {
        return String.format(GET_COMBOBOXES_WITH_MULTIPLE_VALUES_QUERY, entity.getClassName(), field);
    }

    private String prepareSelectDistincQuery(String table, String field) {
        return String.format(SELECT_DISTINCT_QUERY, enquoteIfPostgres(field), enquoteIfPostgres(table), enquoteIfPostgres(field));
    }

    private String enquoteIfPostgres(String string) {
        return persistenceManagerFactory.getConnectionDriverName().equals(MYSQL_DRIVER_CLASSNAME) ? string : "\"" + string + "\"";
    }

    private List<?> executeSQLQuery(String query) {
        return (List) persistenceManagerFactory.getPersistenceManager().newQuery(SQL, query).execute();
    }

    private List<?> executeJDOQLQuery(String query) {
        return (List) persistenceManagerFactory.getPersistenceManager().newQuery(query).execute();
    }

    private List<Field> substract(List<Field> firstList, List<Field> secondList) {

        List<Field> substracted = new ArrayList<>(firstList);

        for (Field field : secondList) {

            Iterator<Field> i = substracted.iterator();

            while (i.hasNext()) {
                Field innerField = i.next();
                if (innerField.getName().equals(field.getName())) {
                    i.remove();
                }
            }
        }

        return substracted;
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
        MotechDataService dataSourceDataService = OSGiServiceUtils.findService(bundleContext,
                MotechClassPool.getInterfaceName(DATA_SOURCE_CLASS_NAME));
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
                throw new LookupReferencedException(entityClassName, lookups.toString());
            }
        }
    }

    @Autowired
    public void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
