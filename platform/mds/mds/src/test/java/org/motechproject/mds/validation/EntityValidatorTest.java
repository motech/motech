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
import org.motechproject.mds.ex.entity.IncompatibleComboboxFieldException;
import org.motechproject.mds.ex.field.FieldUsedInLookupException;
import org.motechproject.mds.ex.lookup.LookupReferencedException;
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
import static org.motechproject.mds.util.Constants.Config.POSTGRES_DRIVER_CLASSNAME;
import static org.motechproject.mds.util.Constants.Settings.ALLOW_MULTIPLE_SELECTIONS;

@RunWith(MockitoJUnitRunner.class)
public class EntityValidatorTest {

    private static final String MYSQL_QUERY = "SELECT * FROM MDS_FOOENTITY_COMBOBOX WHERE IDX != 0";
    private static final String POSTGRES_QUERY = "SELECT * FROM \"MDS_FOOENTITY_COMBOBOX\" WHERE \"IDX\" != 0";

    @Mock
    Entity entity;

    @Mock
    Field fieldOne;

    @Mock
    Field fieldTwo;

    @Mock
    Field combobox;

    @Mock
    Lookup lookupOne;

    @Mock
    Lookup lookupTwo;

    @Mock
    FieldSetting comboboxFieldSetting;

    @Mock
    EntityDraft draft;

    @Mock
    Field draftFieldOne;

    @Mock
    Field draftFieldTwo;

    @Mock
    Field draftCombobox;

    @Mock
    Lookup draftLookupOne;

    @Mock
    Lookup draftLookupTwo;

    @Mock
    FieldSetting draftComboboxFieldSetting;

    @Mock
    BundleContext bundleContext;

    @Mock
    PersistenceManagerFactory persistenceManagerFactory;

    @Mock
    PersistenceManager persistenceManager;

    @Mock
    Query query;

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
    public void shouldValidateComboboxFieldChangeForMySQL() {
        setupPersistenceManager();
        setupPersistenceManagerAsMySQL();
        setupEntity();
        setupDraftEntityForComboboxChange();
        entityValidator.validateEntity(draft);
    }

    @Test(expected = IncompatibleComboboxFieldException.class)
    public void shouldValidateComboboxFieldChangeForPostgres() {
        setupPersistenceManager();
        setupPersistenceManagerAsPostgres();
        setupEntity();
        setupDraftEntityForComboboxChange();
        entityValidator.validateEntity(draft);
    }

    private void setupPersistenceManager() {
        when(persistenceManagerFactory.getPersistenceManager()).thenReturn(persistenceManager);
        when(persistenceManager.newQuery(SQL, MYSQL_QUERY)).thenReturn(query);
        when(persistenceManager.newQuery(SQL, POSTGRES_QUERY)).thenReturn(query);
        when(query.execute()).thenReturn(Arrays.asList("Some results."));
    }

    private void setupPersistenceManagerAsMySQL() {
        when(persistenceManagerFactory.getConnectionDriverName()).thenReturn(MYSQL_DRIVER_CLASSNAME);
    }

    private void setupPersistenceManagerAsPostgres() {
        when(persistenceManagerFactory.getConnectionDriverName()).thenReturn(POSTGRES_DRIVER_CLASSNAME);
    }

    private void setupDraftEntityForComboboxChange() {
        when(draft.getParentEntity()).thenReturn(entity);
        when(draft.getComboboxFields()).thenReturn(Arrays.asList(draftCombobox));
        when(draftCombobox.getName()).thenReturn("combobox");
        when(draftCombobox.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(draftComboboxFieldSetting);
        when(draftComboboxFieldSetting.getValue()).thenReturn(String.valueOf(Boolean.FALSE));
    }

    /*
    entity: {
        fields: [ fieldOne, fieldTwo ]
        lookups: [
            lookupOne: [ fieldOne, fieldTwo ]
            lookupTwo: [ fieldOne ]
        ]
    }
     */
    private void setupEntity() {
        when(entity.getName()).thenReturn("fooEntity");
        when(entity.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(entity.getLookups()).thenReturn(Arrays.asList(lookupOne, lookupTwo));
        when(entity.getField(111L)).thenReturn(fieldOne);
        when(entity.getField(222L)).thenReturn(fieldTwo);
        when(entity.getComboboxFields()).thenReturn(Arrays.asList(combobox));
        when(fieldOne.getId()).thenReturn(111L);
        when(fieldOne.getDisplayName()).thenReturn("field one");
        when(fieldTwo.getId()).thenReturn(222L);
        when(fieldTwo.getDisplayName()).thenReturn("field two");
        when(combobox.getName()).thenReturn("combobox");
        when(combobox.getSettingByName(ALLOW_MULTIPLE_SELECTIONS)).thenReturn(comboboxFieldSetting);
        when(comboboxFieldSetting.getValue()).thenReturn(String.valueOf(Boolean.TRUE));
        when(lookupOne.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(lookupOne.getLookupName()).thenReturn("lookup one");
        when(lookupTwo.getFields()).thenReturn(Arrays.asList(fieldOne));
        when(lookupTwo.getLookupName()).thenReturn("lookup two");
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
        when(bundleContext.getServiceReference("org.motechproject.mds.entity.service.DataSourceService")).thenReturn(dataSourceServiceReference);
        when(bundleContext.getService(dataSourceServiceReference)).thenReturn(dataSourceDataService);
        when(dataSourceDataService.executeQuery(any(QueryExecution.class))).thenReturn(1L);
    }
}
