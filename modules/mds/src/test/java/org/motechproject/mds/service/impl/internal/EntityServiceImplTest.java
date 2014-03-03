package org.motechproject.mds.service.impl.internal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.enhancer.MdsJDOEnhancer;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllEntityDrafts;
import org.motechproject.mds.repository.AllTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Captor
    ArgumentCaptor<Field> fieldCaptor;

    @Captor
    ArgumentCaptor<Lookup> lookupCaptor;

    @InjectMocks
    private EntityServiceImpl entityService = new EntityServiceImpl();

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
    public void shouldAddNewLookup() {
        // given
        long entityId = 1L;
        LookupDto lookupDto = new LookupDto("lookupName", true, false, null, false);
        lookupDto.getFieldNames().add("zero");
        lookupDto.getFieldNames().add("two");

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
        lookupDto.getFieldNames().add("zero");
        lookupDto.getFieldNames().add("two");
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
        Type integer = new Type(Integer.class);

        FieldBasicDto basic = new FieldBasicDto();
        basic.setDisplayName("pi");
        basic.setName("pi");
        basic.setRequired(true);
        basic.setDefaultValue("3.14");
        basic.setTooltip("Sets the value of the PI number");

        FieldDto fieldDto = new FieldDto();
        fieldDto.setEntityId(dto.getId());
        fieldDto.setType(type);
        fieldDto.setBasic(basic);

        // when
        doReturn(entity).when(allEntities).retrieveById(dto.getId());
        doReturn(entityId).when(entity).getId();
        doReturn(integer).when(allTypes).retrieveByClassName(type.getTypeClass());

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
            fields.add(mock(Field.class));
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
}
