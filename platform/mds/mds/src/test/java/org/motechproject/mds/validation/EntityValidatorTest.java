package org.motechproject.mds.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.ex.FieldUsedInLookupException;
import org.motechproject.mds.ex.LookupReferencedException;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.service.MotechDataService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntityValidatorTest {

    @Mock
    Entity entity;

    @Mock
    Field fieldOne;

    @Mock
    Field fieldTwo;

    @Mock
    Lookup lookupOne;

    @Mock
    Lookup lookupTwo;

    @Mock
    EntityDraft draft;

    @Mock
    Field draftFieldOne;

    @Mock
    Field draftFieldTwo;

    @Mock
    Lookup draftLookupOne;

    @Mock
    Lookup draftLookupTwo;

    @Mock
    BundleContext bundleContext;

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
        when(entity.getFields()).thenReturn(Arrays.asList(fieldOne, fieldTwo));
        when(entity.getLookups()).thenReturn(Arrays.asList(lookupOne, lookupTwo));
        when(entity.getField(111L)).thenReturn(fieldOne);
        when(entity.getField(222L)).thenReturn(fieldTwo);
        when(fieldOne.getId()).thenReturn(111L);
        when(fieldOne.getDisplayName()).thenReturn("field one");
        when(fieldTwo.getId()).thenReturn(222L);
        when(fieldTwo.getDisplayName()).thenReturn("field two");
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
