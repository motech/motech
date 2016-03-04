package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.UserPreferencesDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.exception.entity.EntityAlreadyExistException;
import org.motechproject.mds.exception.field.FieldUsedInLookupException;
import org.motechproject.mds.exception.lookup.LookupReferencedException;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllEntityAudits;
import org.motechproject.mds.repository.AllEntityDrafts;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.UserPreferencesService;
import org.motechproject.mds.testutil.DraftBuilder;
import org.motechproject.mds.validation.EntityValidator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.FieldTestHelper.lookupFieldDto;
import static org.motechproject.mds.util.Constants.PackagesGenerated.ENTITY;

@RunWith(MockitoJUnitRunner.class)
public class EntityServiceImplTest {
    private static final String CLASS_NAME = String.format("%s.Sample", ENTITY);

    @Mock
    private AllEntities allEntities;

    @Mock
    private AllEntityDrafts allEntityDrafts;

    @Mock
    private AllTypes allTypes;

    @Mock
    private MdsJDOEnhancer enhancer;

    @Mock
    private EntityDto entityDto;

    @Mock
    private Entity entity;

    @Mock
    private EntityDraft draft;

    @Mock
    private AllEntityAudits allEntityAudits;

    @Mock
    private Field field;

    @Mock
    private FieldDto fieldDto;

    @Mock
    private FieldBasicDto basic;

    @Mock
    private Field fieldSecond;

    @Mock
    private Lookup lookup;

    @Mock
    private Lookup draftLookup;

    @Mock
    private UserPreferencesService userPreferencesService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference dataSourceServiceReference;

    @Mock
    private MotechDataService dataSourceDataService;

    @Captor
    ArgumentCaptor<Field> fieldCaptor;

    @Captor
    ArgumentCaptor<Lookup> lookupCaptor;

    @Spy
    @InjectMocks
    private EntityValidator entityValidator = new EntityValidator();

    @InjectMocks
    private EntityServiceImpl entityService = new EntityServiceImpl();

    @Before
    public void setUp() {
        setUpSecurityContext();
    }

    @Test(expected = EntityAlreadyExistException.class)
    public void shouldNotCreateTwiceSameEntity() throws Exception {
        when(entityDto.getClassName()).thenReturn(CLASS_NAME);
        when(allEntities.contains(CLASS_NAME)).thenReturn(true);

        entityService.createEntity(entityDto);
    }

    @Test
    public void shouldDeleteDraftsAndEntities() {
        when(allEntities.retrieveById(1L)).thenReturn(entity);

        entityService.deleteEntity(1L);

        verify(allEntityDrafts).deleteAll(entity);
        verify(allEntities).delete(entity);
    }

    @Test
    public void shouldCreateValidClassNames() throws IOException {
        EntityDto entityDto1 = new EntityDto(null, null, "test name with spaces", null, null, null, null);
        EntityDto entityDto2 = new EntityDto(null, null, "    second test      with spaces", null, null, null, null);
        EntityDto entityDto3 = new EntityDto(null, null, "Sample name  ", null, null, null, null);

        when(entity.getField((String) any())).thenReturn(null);
        when(allEntities.create((EntityDto) any())).thenReturn(entity);

        EntityDto entityDto4 = new EntityDto(null, "org.motechproject.mds.entity.TestNameWithSpaces", "test name with spaces", null, null, null, null);
        entityService.createEntity(entityDto1);
        verify(allEntities, times(1)).create(entityDto4);

        entityDto4 = new EntityDto(null, "org.motechproject.mds.entity.SecondTestWithSpaces", "second test      with spaces", null, null, null, null);
        entityService.createEntity(entityDto2);
        verify(allEntities, times(1)).create(entityDto4);

        entityDto4 = new EntityDto(null, "org.motechproject.mds.entity.SampleName", "Sample name", null, null, null, null);
        entityService.createEntity(entityDto3);
        verify(allEntities, times(1)).create(entityDto4);
    }

    @Test
    public void shouldAddNewLookup() {
        // given
        long entityId = 1L;
        LookupDto lookupDto = new LookupDto("lookupName", true, false, null, false);
        lookupDto.getLookupFields().add(lookupFieldDto("zero"));
        lookupDto.getLookupFields().add(lookupFieldDto("two"));

        int fieldCount = 4;
        List<Field> fields = new ArrayList<>();
        String[] fieldNames = {"zero", "one", "two", "three"};

        for (long idx = 0; idx < fieldCount; ++idx) {
            Field field = mock(Field.class);

            doReturn(idx).when(field).getId();
            doReturn(field).when(entity).getField(idx);

            String fieldName = fieldNames[(int) idx];
            doReturn(field).when(entity).getField(fieldName);
            doReturn(fieldName).when(field).getName();

            if (idx % 2 == 0) {
                fields.add(field);
            }
        }

        // when
        doReturn(entity).when(allEntities).retrieveById(entityId);
        doReturn(null).when(entity).getLookupById(anyLong());

        entityService.addLookups(entityId, asList(lookupDto));

        // then
        verify(allEntities).retrieveById(entityId);
        verify(entity).getLookupByName("lookupName");
        verify(entity).addLookup(lookupCaptor.capture());

        for (long idx = 0; idx < fieldCount; ++idx) {
            int wantedNumberOfInvocations = (int) Math.abs(idx % 2 - 1);
            verify(entity, times(wantedNumberOfInvocations)).getField(fieldNames[(int) idx]);
        }

        Lookup lookup = lookupCaptor.getValue();

        assertEquals(lookupDto.getLookupName(), lookup.getLookupName());
        assertEquals(lookupDto.isSingleObjectReturn(), lookup.isSingleObjectReturn());
        assertEquals(lookupDto.isExposedViaRest(), lookup.isExposedViaRest());
        assertEquals(fields, lookup.getFields());
    }

    @Test
    public void shouldUpdateExistingLookup() {
        // given
        long entityId = 1L;
        LookupDto lookupDto = new LookupDto("lookupName", true, false, null, false);
        lookupDto.getLookupFields().add(lookupFieldDto("zero"));
        lookupDto.getLookupFields().add(lookupFieldDto("two"));
        Lookup lookup = new Lookup();

        int fieldCount = 4;
        List<Field> fields = new ArrayList<>();
        String[] fieldNames = {"zero", "one", "two", "three"};

        for (long idx = 0; idx < fieldCount; ++idx) {
            Field field = mock(Field.class);

            doReturn(idx).when(field).getId();
            doReturn(field).when(entity).getField(idx);

            String fieldName = fieldNames[(int) idx];
            doReturn(field).when(entity).getField(fieldName);
            doReturn(fieldName).when(field).getName();

            if (idx % 2 == 0) {
                fields.add(field);
            }
        }

        // when
        doReturn(entity).when(allEntities).retrieveById(entityId);
        doReturn(lookup).when(entity).getLookupByName("lookupName");

        entityService.addLookups(entityId, asList(lookupDto));

        // then
        verify(allEntities).retrieveById(entityId);
        verify(entity).getLookupByName("lookupName");
        verify(entity, never()).addLookup(any(Lookup.class));

        assertEquals(lookupDto.getLookupName(), lookup.getLookupName());
        assertEquals(lookupDto.isSingleObjectReturn(), lookup.isSingleObjectReturn());
        assertEquals(lookupDto.isExposedViaRest(), lookup.isExposedViaRest());
        assertEquals(fields, lookup.getFields());
    }

    @Test
    public void shouldAddNewField() {
        // given
        long entityId = 1L;
        EntityDto dto = new EntityDto(entityId, CLASS_NAME);
        TypeDto type = TypeDto.INTEGER;
        Type integerType = new Type(Integer.class);

        FieldBasicDto basic = new FieldBasicDto();
        basic.setDisplayName("pi");
        basic.setName("pi");
        basic.setRequired(true);
        basic.setDefaultValue("3.14");
        basic.setTooltip("Sets the value of the PI number");
        basic.setPlaceholder("3.14");

        FieldDto fieldDto = new FieldDto();
        fieldDto.setEntityId(dto.getId());
        fieldDto.setType(type);
        fieldDto.setBasic(basic);

        // when
        doReturn(entity).when(allEntities).retrieveById(dto.getId());
        doReturn(entityId).when(entity).getId();
        doReturn(integerType).when(allTypes).retrieveByClassName(type.getTypeClass());

        entityService.addFields(dto, asList(fieldDto));

        // then
        verify(allEntities).retrieveById(dto.getId());
        verify(entity).addField(fieldCaptor.capture());

        Field field = fieldCaptor.getValue();

        assertEquals(basic.getName(), field.getName());
        assertEquals(basic.getDisplayName(), field.getDisplayName());
        assertEquals(basic.isRequired(), field.isRequired());
        assertEquals(basic.getDefaultValue(), field.getDefaultValue());
        assertEquals(basic.getTooltip(), field.getTooltip());
        assertEquals(basic.getPlaceholder(), field.getPlaceholder());
        assertEquals(fieldDto.getType().getTypeClass(), field.getType().getTypeClass().getName());
        assertEquals(fieldDto.getEntityId(), field.getEntity().getId());
    }

    @Test
    public void shouldUpdateExistingField() {
        // given
        long entityId = 1L;
        EntityDto dto = new EntityDto(entityId, CLASS_NAME);
        TypeDto type = TypeDto.INTEGER;
        Field field = mock(Field.class);

        FieldBasicDto basic = new FieldBasicDto();
        basic.setDisplayName("pi");
        basic.setName("pi");
        basic.setRequired(true);
        basic.setDefaultValue("3.14");
        basic.setTooltip("Sets the value of the PI number");
        basic.setPlaceholder("3.14");

        FieldDto fieldDto = new FieldDto();
        fieldDto.setEntityId(dto.getId());
        fieldDto.setType(type);
        fieldDto.setBasic(basic);

        // when
        doReturn(entity).when(allEntities).retrieveById(dto.getId());
        doReturn(entityId).when(entity).getId();
        doReturn(field).when(entity).getField(basic.getName());

        entityService.addFields(dto, asList(fieldDto));

        // then
        verify(allEntities).retrieveById(dto.getId());
        verify(entity).getField(basic.getName());
        verify(entity, never()).addField(any(Field.class));
        verify(field).update(fieldDto);
    }

    @Test
    public void shouldSetUIFilterableOnlyToCorrectFields() {
        // given
        Field filterableField = mock(Field.class);
        Field nonFilterableField = mock(Field.class);

        // #1 when
        doReturn("filterableField").when(filterableField).getName();
        doReturn("nonFilterableField").when(nonFilterableField).getName();

        doReturn(1L).when(entityDto).getId();
        doReturn(entity).when(allEntities).retrieveById(1L);
        doReturn(asList(filterableField, nonFilterableField)).when(entity).getFields();

        entityService.addFilterableFields(entityDto, asList("filterableField"));

        // #1 then
        verify(allEntities).retrieveById(1L);
        verify(entity).getFields();
        verify(filterableField).setUIFilterable(true);
        verify(nonFilterableField).setUIFilterable(false);

        // #2 when
        entityService.addFilterableFields(entityDto, asList("nonFilterableField"));

        // #2 then
        verify(allEntities, times(2)).retrieveById(1L);
        verify(entity, times(2)).getFields();
        verify(filterableField).setUIFilterable(false);
        verify(nonFilterableField).setUIFilterable(true);

        // #3 when
        entityService.addFilterableFields(entityDto, asList("filterableField", "nonFilterableField"));

        // #3 then
        verify(allEntities, times(3)).retrieveById(1L);
        verify(entity, times(3)).getFields();
        verify(filterableField, times(2)).setUIFilterable(true);
        verify(nonFilterableField, times(2)).setUIFilterable(true);

        // #4 when
        entityService.addFilterableFields(entityDto, new ArrayList<String>());

        // #4 then
        verify(allEntities, times(4)).retrieveById(1L);
        verify(entity, times(4)).getFields();
        verify(filterableField, times(2)).setUIFilterable(false);
        verify(nonFilterableField, times(2)).setUIFilterable(false);
    }

    @Test
    public void shouldSetUIDisplayableToAllFieldsIfMapIsEmpty() {
        // given
        int fieldCount = 10;
        List<Field> fields = new ArrayList<>(fieldCount);

        for (int i = 0; i < fieldCount; ++i) {
            Field fieldMock = mock(Field.class);
            when(fieldMock.isReadOnly()).thenReturn(true);
            fields.add(fieldMock);
        }

        // #1 when
        doReturn(1L).when(entityDto).getId();
        doReturn(entity).when(allEntities).retrieveById(1L);
        doReturn(fields).when(entity).getFields();

        entityService.addDisplayedFields(entityDto, null);

        // then
        verify(allEntities).retrieveById(1L);
        verify(entity).getFields();

        for (int i = 0; i < fieldCount; ++i) {
            Field field = fields.get(i);

            verify(field).setUIDisplayable(true);
            verify(field).setUIDisplayPosition((long) i);
        }

        // #2 when
        entityService.addDisplayedFields(entityDto, new HashMap<String, Long>());

        // #2 then
        verify(allEntities, times(2)).retrieveById(1L);
        verify(entity, times(2)).getFields();

        for (int i = 0; i < fieldCount; ++i) {
            Field field = fields.get(i);

            verify(field, times(2)).setUIDisplayable(true);
            verify(field, times(2)).setUIDisplayPosition((long) i);
        }
    }

    @Test
    public void shouldSetUIDisplayableOnlyToCorrectFields() {
        // given
        int fieldCount = 12;
        long displayPosition = fieldCount / 4 - 1;
        List<Field> fields = new ArrayList<>(fieldCount);
        Map<String, Long> positions = new HashMap<>();

        for (int i = 0; i < fieldCount; ++i) {
            Field field = mock(Field.class);

            // every 4th field will have set the uiDisplayable flag
            if (i % 4 == 0) {
                String fieldName = "field" + i;

                doReturn(fieldName).when(field).getName();
                positions.put(fieldName, displayPosition);

                --displayPosition;
            }

            fields.add(field);
        }

        // when
        doReturn(1L).when(entityDto).getId();
        doReturn(entity).when(allEntities).retrieveById(1L);
        doReturn(fields).when(entity).getFields();

        entityService.addDisplayedFields(entityDto, positions);

        // then
        verify(allEntities).retrieveById(1L);
        verify(entity).getFields();

        for (int i = 0; i < fieldCount; ++i) {
            Field field = fields.get(i);

            // every 4th field should have set the uiDisplayable flag; for other fields this
            // flag should be unset
            if (i % 4 == 0) {
                verify(field).setUIDisplayable(true);
                verify(field).setUIDisplayPosition(positions.get("field" + i));
            } else {
                verify(field).setUIDisplayable(false);
                verify(field).setUIDisplayPosition(null);
            }
        }
    }

    @Test
    public void shouldMarkUniqueChangesInDraft() {
        DraftData dd = new DraftData();
        dd.setEdit(true);
        dd.getValues().put(DraftData.VALUE, singletonList(true));
        dd.getValues().put(DraftData.PATH, "basic.unique");
        dd.getValues().put(DraftData.FIELD_ID, 2L);
        when(allEntities.retrieveById(1L)).thenReturn(entity);
        when(allEntityDrafts.retrieve(entity, "motech")).thenReturn(draft);
        when(draft.getField(2L)).thenReturn(field);
        when(draft.getParentEntity()).thenReturn(entity);
        when(entity.getField("fieldName")).thenReturn(field);
        when(field.getName()).thenReturn("fieldName");
        when(field.toDto()).thenReturn(fieldDto);
        when(fieldDto.getBasic()).thenReturn(basic);
        when(field.isUnique()).thenReturn(true);

        entityService.saveDraftEntityChanges(1L, dd);

        verify(field).update(any(FieldDto.class));
        verify(draft, never()).addUniqueToRemove(anyString());

        // test marking for removal

        dd.getValues().put(DraftData.VALUE, singletonList(false));

        entityService.saveDraftEntityChanges(1L, dd);

        verify(draft).addUniqueToRemove("fieldName");
    }

    @Test(expected = FieldUsedInLookupException.class)
    public void shouldNotAllowRemovingAFieldUsedInALookup() {
        when(field.getId()).thenReturn(456L);
        when(lookup.getLookupName()).thenReturn("Lookup name");
        when(field.getDisplayName()).thenReturn("Field Name");
        when(lookup.getFields()).thenReturn(asList(field));
        when(draft.getFields()).thenReturn(asList(field));
        when(draft.getLookups()).thenReturn(asList(lookup));
        when(draft.getId()).thenReturn(8L);
        when(draft.getField(456L)).thenReturn(field);
        when(allEntities.retrieveById(8L)).thenReturn(entity);
        when(allEntityDrafts.retrieve(eq(entity), anyString())).thenReturn(draft);

        DraftData dd = DraftBuilder.forFieldRemoval(456L);

        entityService.saveDraftEntityChanges(8L, dd);
    }

    @Test(expected = LookupReferencedException.class)
    public void shouldNotAllowRemoveFieldOfLookupUsedInTask() {
        when(bundleContext.getServiceReference("org.motechproject.tasks.domain.mdsservice.DataSourceService")).thenReturn(dataSourceServiceReference);
        when(bundleContext.getService(dataSourceServiceReference)).thenReturn(dataSourceDataService);
        when(dataSourceDataService.executeQuery(any(QueryExecution.class))).thenReturn(1L);
        when(field.getId()).thenReturn(456L);
        when(fieldSecond.getId()).thenReturn(789L);

        when(lookup.getLookupName()).thenReturn("Lookup name");
        when(draftLookup.getLookupName()).thenReturn("Lookup name");

        when(lookup.getFields()).thenReturn(asList(field, fieldSecond));
        when(draftLookup.getFields()).thenReturn(asList(field));

        when(entity.getFields()).thenReturn(asList(field, fieldSecond));
        when(draft.getFields()).thenReturn(asList(field, fieldSecond));

        when(entity.getLookups()).thenReturn(asList(lookup));
        when(draft.getLookups()).thenReturn(asList(draftLookup));

        when(draft.getParentEntity()).thenReturn(entity);
        when(draft.getId()).thenReturn(8L);

        when(allEntities.retrieveById(8L)).thenReturn(entity);
        when(allEntityDrafts.retrieve(eq(entity), anyString())).thenReturn(draft);

        when(userPreferencesService.getEntityPreferences(8l)).thenReturn(new ArrayList<UserPreferencesDto>());

        entityService.commitChanges(8L);
    }

    private void setUpSecurityContext() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("mdsSchemaAccess");
        List<SimpleGrantedAuthority> authorities = asList(authority);

        User principal = new User("motech", "motech", authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null);
        authentication.setAuthenticated(false);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
