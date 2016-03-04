package org.motechproject.mds.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.exception.UserSuppliedComboboxValuesUsedException;
import org.motechproject.mds.exception.entity.IncompatibleComboboxFieldException;
import org.motechproject.mds.exception.field.FieldUsedInLookupException;
import org.motechproject.mds.exception.lookup.LookupReferencedException;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Arrays;

import static javax.jdo.Query.SQL;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.util.Constants.Config.MYSQL_DRIVER_CLASSNAME;
import static org.motechproject.mds.util.Constants.Settings.ALLOW_MULTIPLE_SELECTIONS;
import static org.motechproject.mds.util.Constants.Settings.COMBOBOX_VALUES;

@RunWith(MockitoJUnitRunner.class)
public class EntityValidatorTest {

    private static final String SELECT_INSTANCES_WITH_MULTIPLE_VALUES_QUERY = "SELECT FROM org.motechproject.mds.entity.FooEntity WHERE comboboxOne.size() > 1";
    private static final String SELECT_DISTINCT_SINGLESELECT_VALUES_QUERY = "SELECT DISTINCT comboboxTwo FROM MDS_FOOENTITY WHERE comboboxTwo IS NOT NULL";
    private static final String SELECT_DISTINCT_MULTISELECT_VALUES_QUERY = "SELECT DISTINCT ELEMENT FROM MDS_FOOENTITY_COMBOBOXONE WHERE ELEMENT IS NOT NULL";

    @Mock
    Entity entity;

    @Mock
    Field fieldOne;

    @Mock
    Field fieldTwo;

    @Mock
    Field comboboxOne;
    
    @Mock
    Field comboboxTwo;
    
    @Mock
    Lookup lookupOne;

    @Mock
    Lookup lookupTwo;

    @Mock
    FieldSetting comboboxOneMultiselectSetting;

    @Mock
    FieldSetting comboboxTwoMultiselectSetting;

    @Mock
    EntityDraft draft;

    @Mock
    Field draftFieldOne;

    @Mock
    Field draftFieldTwo;

    @Mock
    Field draftComboboxOne;
    
    @Mock
    Field draftComboboxTwo;

    @Mock
    Lookup draftLookupOne;

    @Mock
    Lookup draftLookupTwo;

    @Mock
    FieldSetting draftComboboxOneSetting;

    @Mock
    FieldSetting draftComboboxTwoSetting;

    @Mock
    FieldSetting draftComboboxOneValuesSetting;

    @Mock
    FieldSetting draftComboboxTwoValuesSetting;


    @Mock
    BundleContext bundleContext;

    @Mock
    PersistenceManagerFactory persistenceManagerFactory;

    @Mock
    PersistenceManager persistenceManager;

    @Mock
    Query query;

    @Mock
    Query selectDistinctQuery;

    @Mock
    private ServiceReference dataSourceServiceReference;

    @Mock
    private MotechDataService dataSourceDataService;

    @InjectMocks
    EntityValidator entityValidator = new EntityValidator();

    @Test(expected = FieldUsedInLookupException.class)
    public void shouldValidateFieldUsedByLookup() {
        setupEntity();
        entityValidator.validateFieldNotUsedByLookups(entity, 111L);
    }

    @Test(expected = LookupReferencedException.class)
    public void shouldValidateEntityLookupFieldRemoval() {
        setupDataSource();
        setupEntity();
        setupDraftEntityForFieldRemoval();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = LookupReferencedException.class)
    public void shouldValidateEntityLookupFieldAddition() {
        setupDataSource();
        setupEntity();
        setupDraftEntityForFieldAddition();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = LookupReferencedException.class)
    public void shouldValidateEntityLookupChange() {
        setupDataSource();
        setupEntity();
        setupDraftEntityForLookupChange();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = IncompatibleComboboxFieldException.class)
    public void shouldValidateComboboxFieldChangeL() {
        setupPersistenceManager();
        setupEntity();
        setupBaseDraft();
        setupDraftEntityForComboboxChange();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = UserSuppliedComboboxValuesUsedException.class)
    public void shouldValidateEntitySingleSelectComboboxValues() {
        setupPersistenceManager();
        setupPersistenceManagerAsMySQL();
        setupEntity();
        setupBaseDraft();
        setupDraftEntityForComboboxFieldValidation();
        setupSingleSelectFieldChange();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = UserSuppliedComboboxValuesUsedException.class)
    public void shouldValidateEntityMultiSelectComboboxValues() {
        setupPersistenceManager();
        setupPersistenceManagerAsMySQL();
        setupEntity();
        setupBaseDraft();
        setupDraftEntityForComboboxFieldValidation();
        setupMultiSelectFieldChange();
        entityValidator.validateEntity(draft);
    }

    private void setupPersistenceManager() {
        when(persistenceManagerFactory.getPersistenceManager()).thenReturn(persistenceManager);
        when(persistenceManager.newQuery(SELECT_INSTANCES_WITH_MULTIPLE_VALUES_QUERY)).thenReturn(query);
        when(persistenceManager.newQuery(SQL, SELECT_DISTINCT_SINGLESELECT_VALUES_QUERY)).thenReturn(selectDistinctQuery);
        when(persistenceManager.newQuery(SQL, SELECT_DISTINCT_MULTISELECT_VALUES_QUERY)).thenReturn(selectDistinctQuery);
        when(query.execute()).thenReturn(Arrays.asList("Some results."));
        when(selectDistinctQuery.execute()).thenReturn(Arrays.asList("FooValue", "UserSupplied1"));
    }


    /*
        entity: {
            fields: [ fieldOne, fieldTwo, comboboxOne, comboboxTwo ]
            lookups: [
                lookupOne: [ fieldOne, fieldTwo ]
                lookupTwo: [ fieldOne ]
            ]
        }
    */
    private void setupEntity() {
        when(entity.getName()).thenReturn("FooEntity");
        when(entity.getClassName()).thenReturn("org.motechproject.mds.entity.FooEntity");
        when(entity.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(entity.getLookups()).thenReturn(Arrays.asList(lookupOne, lookupTwo));
        when(entity.getField(111L)).thenReturn(fieldOne);
        when(entity.getField(222L)).thenReturn(fieldTwo);
        when(entity.getField("comboboxOne")).thenReturn(comboboxOne);
        when(entity.getField("comboboxTwo")).thenReturn(comboboxTwo);
        when(entity.getComboboxFields()).thenReturn(Arrays.asList(comboboxOne, comboboxTwo));
        when(entity.getStringComboboxFields()).thenReturn(Arrays.asList(comboboxOne, comboboxTwo));

        when(fieldOne.getId()).thenReturn(111L);
        when(fieldOne.getDisplayName()).thenReturn("field one");

        when(fieldTwo.getId()).thenReturn(222L);
        when(fieldTwo.getDisplayName()).thenReturn("field two");

        when(comboboxOne.getName()).thenReturn("comboboxOne");
        when(comboboxOne.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(comboboxOneMultiselectSetting);
        when(comboboxOne.isMultiSelectCombobox()).thenReturn(Boolean.TRUE);
        when(comboboxOneMultiselectSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));

        when(comboboxTwo.getName()).thenReturn("comboboxTwo");
        when(comboboxTwo.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(comboboxTwoMultiselectSetting);
        when(comboboxTwo.isMultiSelectCombobox()).thenReturn(Boolean.FALSE);
        when(comboboxTwoMultiselectSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));

        when(lookupOne.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(lookupOne.getLookupName()).thenReturn("lookup one");

        when(lookupTwo.getFields()).thenReturn(Arrays.asList(fieldOne));
        when(lookupTwo.getLookupName()).thenReturn("lookup two");
    }

    /*
        draft: {
            fields: [ draftComboboxOne, draftComboboxTwo ]
        }
     */
    private void setupBaseDraft() {

        when(draft.getName()).thenReturn("FooEntity");
        when(draft.getParentEntity()).thenReturn(entity);
        when(draft.getField("comboboxOne")).thenReturn(draftComboboxOne);
        when(draft.getField("comboboxTwo")).thenReturn(draftComboboxTwo);
        when(draft.getComboboxFields()).thenReturn(Arrays.asList(draftComboboxOne, draftComboboxTwo));
        when(draft.getStringComboboxFields()).thenReturn(Arrays.asList(draftComboboxOne, draftComboboxTwo));

        when(draftComboboxOne.getName()).thenReturn("comboboxOne");
        when(draftComboboxOne.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(draftComboboxOneSetting);
        when(draftComboboxOne.getSettingByName(COMBOBOX_VALUES)).thenReturn(draftComboboxOneValuesSetting);
        when(draftComboboxTwo.getSettingByName(COMBOBOX_VALUES)).thenReturn(draftComboboxTwoValuesSetting);


        when(draftComboboxTwo.getName()).thenReturn("comboboxTwo");
        when(draftComboboxTwo.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(draftComboboxTwoSetting);

        when(draftComboboxOneValuesSetting.getValue()).thenReturn("FooValue");
        when(draftComboboxTwoValuesSetting.getValue()).thenReturn("FooValue");
    }

    private void setupSingleSelectFieldChange() {
        when(draftComboboxOneSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));
        when(draftComboboxTwoSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));

        when(draft.getStringComboboxFields()).thenReturn(Arrays.asList(draftComboboxOne));
    }

    private void setupMultiSelectFieldChange() {
        when(draftComboboxOneSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));
        when(draftComboboxTwoSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));

        when(draft.getStringComboboxFields()).thenReturn(Arrays.asList(draftComboboxTwo));
    }

    private void setupDraftEntityForComboboxFieldValidation() {
        when(draftComboboxOne.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(draftComboboxOneSetting);
        when(draftComboboxTwo.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(draftComboboxTwoSetting);

        when(draftComboboxOneSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));
        when(draftComboboxTwoSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));
    }

    private void setupPersistenceManagerAsMySQL() {
        when(persistenceManagerFactory.getConnectionDriverName()).thenReturn(MYSQL_DRIVER_CLASSNAME);
    }

    private void setupDraftEntityForComboboxChange() {
        when(draftComboboxOneSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));
        when(draftComboboxTwoSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));
    }

    /*
    draft: {
        parent: entity
        fields: [ draftFieldOne, draftFieldTwo ]
        lookups: [
            draftLookupOne [ draftFieldTwo ]
            draftLookupTwo [ draftFieldOne ]
        ]
    }
     */
    private void setupDraftEntityForFieldRemoval() {
        setupDraftEntity();
        setupDraftFields();
        when(draftLookupOne.getFields()).thenReturn(Arrays.asList(fieldTwo));
        when(draftLookupOne.getLookupName()).thenReturn("lookup one");
        when(draftLookupTwo.getFields()).thenReturn(Arrays.asList(fieldOne));
        when(draftLookupTwo.getLookupName()).thenReturn("lookup two");
    }

    /*
    entity: {
        fields: [ fieldOne, fieldTwo ]
        lookups: [
            lookupOne: [ fieldOne, fieldTwo ]
            lookupTwo: [ fieldOne, fieldTwo ]
        ]
    }
     */
    private void setupDraftEntityForFieldAddition() {
        setupDraftEntity();
        setupDraftFields();
        when(draftLookupOne.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(draftLookupOne.getLookupName()).thenReturn("lookup one");
        when(draftLookupTwo.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(draftLookupTwo.getLookupName()).thenReturn("lookup two");
    }

    private void setupDraftEntityForLookupChange() {
        setupDraftEntity();
        setupDraftFields();
        when(draftLookupOne.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(draftLookupOne.getLookupName()).thenReturn("lookup changed");
        when(draftLookupTwo.getFields()).thenReturn(Arrays.asList(fieldOne));
        when(draftLookupTwo.getLookupName()).thenReturn("lookup two");
    }

    private void setupDraftEntity() {
        when(draft.getParentEntity()).thenReturn(entity);
        when(draft.getFields()).thenReturn(Arrays.asList(draftFieldOne, draftFieldTwo));
        when(draft.getLookups()).thenReturn(Arrays.asList(draftLookupOne, draftLookupTwo));
        when(draft.getField(1111L)).thenReturn(draftFieldOne);
        when(draft.getField(2222L)).thenReturn(draftFieldTwo);
        when(draft.getComboboxFields()).thenReturn(new ArrayList<Field>());
    }

    private void setupDraftFields() {
        when(draftFieldOne.getId()).thenReturn(1111L);
        when(draftFieldOne.getDisplayName()).thenReturn("field one");
        when(draftFieldTwo.getId()).thenReturn(2222L);
        when(draftFieldTwo.getDisplayName()).thenReturn("field two");
    }

    private void setupDataSource() {
        when(bundleContext.getServiceReference("org.motechproject.tasks.domain.mdsservice.DataSourceService")).thenReturn(dataSourceServiceReference);
        when(bundleContext.getService(dataSourceServiceReference)).thenReturn(dataSourceDataService);
        when(dataSourceDataService.executeQuery(any(QueryExecution.class))).thenReturn(1L);
    }
}
