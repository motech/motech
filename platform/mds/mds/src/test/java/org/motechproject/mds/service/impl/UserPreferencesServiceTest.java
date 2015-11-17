package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Lookup;
import org.motechproject.mds.domain.UserPreferences;
import org.motechproject.mds.ex.field.FieldNotFoundException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllUserPreferences;
import org.motechproject.mds.service.UserPreferencesService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPreferencesServiceTest {

    private static final String USERNAME = "username";
    private static final String CLASS_NAME = "org.motechproject.sample.ClassName";

    @Mock
    private AllUserPreferences allUserPreferences;

    @Mock
    private AllEntities allEntities;

    @Mock
    private SettingsService settingsService;

    @Mock
    private UserPreferences userPreferences;

    @Mock
    Entity entity;

    @Captor
    ArgumentCaptor<UserPreferences> userPreferencesCaptor;

    @Captor
    ArgumentCaptor<List<Field>> fieldsCaptor;

    @Captor
    ArgumentCaptor<Field> fieldCaptor;

    @InjectMocks
    private UserPreferencesService userPreferencesService = new UserPreferencesServiceImpl();

    @Before
    public void setUp() {
        when(settingsService.getGridSize()).thenReturn(20);
        when(entity.getFields()).thenReturn(createFields());
        when(entity.getClassName()).thenReturn(CLASS_NAME);
        when(allEntities.retrieveById(15l)).thenReturn(entity);
    }

    @Test
    public void shouldCreateDefaultPreferences() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(null);

        userPreferencesService.getUserPreferences(15l, USERNAME);

        verify(allUserPreferences).create(userPreferencesCaptor.capture());
        UserPreferences capturedPreferences = userPreferencesCaptor.getValue();

        assertEquals(USERNAME, capturedPreferences.getUsername());
        assertEquals(CLASS_NAME, capturedPreferences.getClassName());
        assertNotNull(capturedPreferences.getVisibleFields().size());
        assertEquals(2, capturedPreferences.getVisibleFields().size());
        assertEquals("sampleField1", capturedPreferences.getVisibleFields().get(0).getName());
        assertEquals("sampleField2", capturedPreferences.getVisibleFields().get(1).getName());
    }

    @Test
    public void shouldUpdateGridSize() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);

        userPreferencesService.updateGridSize(15l, USERNAME, 50);

        verify(userPreferences).setGridRowsNumber(50);
        verify(allUserPreferences).update(userPreferences);
    }


    @Test
    public void shouldTakeGridSizeFromSettingsService() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);

        userPreferencesService.updateGridSize(15l, USERNAME, null);

        verify(userPreferences).setGridRowsNumber(20);
        verify(allUserPreferences).update(userPreferences);
    }


    @Test(expected = FieldNotFoundException.class)
    public void shouldCheckFieldSelectField() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);

        userPreferencesService.selectField(15l, USERNAME, "oldField");

        verify(userPreferences).setGridRowsNumber(20);
        verify(allUserPreferences).update(userPreferences);
    }

    @Test
    public void shouldSelectField() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);
        when(entity.getField("sampleField1")).thenReturn(getField1());

        userPreferencesService.selectField(15l, USERNAME, "sampleField1");

        verify(userPreferences).addField(fieldCaptor.capture());
        verify(allUserPreferences).update(userPreferences);

        Field field = fieldCaptor.getValue();
        assertNotNull(field);
        assertEquals("sampleField1", field.getName());
    }

    @Test(expected = FieldNotFoundException.class)
    public void shouldCheckFieldWhenUnselectField() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);

        userPreferencesService.unselectField(15l, USERNAME, "oldField");

        verify(userPreferences).setGridRowsNumber(20);
        verify(allUserPreferences).update(userPreferences);
    }

    @Test
    public void shouldUnselectField() {
        Field field1 = getField1();

        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);
        when(entity.getField("sampleField1")).thenReturn(field1);

        userPreferencesService.unselectField(15l, USERNAME, "sampleField1");

        verify(userPreferences).removeField(fieldCaptor.capture());
        verify(allUserPreferences).update(userPreferences);

        Field field = fieldCaptor.getValue();
        assertNotNull(field);
        assertEquals("sampleField1", field.getName());
    }

    @Test
    public void shouldSelectFields() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);
        when(entity.getField("sampleField1")).thenReturn(getField1());
        when(userPreferences.getVisibleFields()).thenReturn(new ArrayList<Field>());

        userPreferencesService.selectFields(15l, USERNAME);

        verify(userPreferences).setVisibleFields(fieldsCaptor.capture());
        verify(allUserPreferences).update(userPreferences);

        List<Field> fields = fieldsCaptor.getValue();
        assertNotNull(fields);
        assertEquals(4, fields.size());
        assertEquals("sampleField1", fields.get(0).getName());
        assertEquals("sampleField2", fields.get(1).getName());
        assertEquals("sampleField3", fields.get(2).getName());
        assertEquals("sampleField4", fields.get(3).getName());
    }

    @Test
    public void shouldUnselectFields() {
        when(allUserPreferences.retrieveByClassNameAndUsername(CLASS_NAME, USERNAME)).thenReturn(userPreferences);
        when(entity.getField("sampleField1")).thenReturn(getField1());
        when(userPreferences.getVisibleFields()).thenReturn(createFields());

        userPreferencesService.unselectFields(15l, USERNAME);

        verify(userPreferences).setVisibleFields(fieldsCaptor.capture());
        verify(allUserPreferences).update(userPreferences);

        List<Field> fields = fieldsCaptor.getValue();
        assertNotNull(fields);
        assertEquals(0, fields.size());
    }

    private List<Field> createFields() {
        List<Field> fields = new ArrayList<>();
        fields.add(getField1());
        fields.add(getField2());
        fields.add(getField3());
        fields.add(getField4());
        return fields;
    }

    private Field getField1() {
        Field field =  new Field(null, "sampleField1", "Display Name 1", true, false, false, false, false, false, "default 1", "tooltip 1", "placeholder 1", new HashSet<Lookup>());
        field.setUIDisplayable(true);
        return field;
    }

    private Field getField2() {
        Field field = new Field(null, "sampleField2", "Display Name 2", true, false, false, false, false, false, "default 2", "tooltip 2", "placeholder 2", new HashSet<Lookup>());
        field.setUIDisplayable(true);
        return field;
    }


    private Field getField3() {
        return new Field(null, "sampleField3", "Display Name 3", true, false, false, false, false, false, "default 3", "tooltip 3", "placeholder 3", new HashSet<Lookup>());
    }

    private Field getField4() {
        return new Field(null, "sampleField4", "Display Name 4", true, false, false, true, false, false, "default 4", "tooltip 4", "placeholder 4", new HashSet<Lookup>());
    }
}
