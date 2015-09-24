package org.motechproject.mds.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComboboxValueHelperTest {

    private static final List<String> PREDEFINED_VALUES = asList("one", "two", "three");
    private static final List<String> VALUES_FROM_REPOSITORY = asList("one", "two", "four", "five");
    private static final List<String> MERGED_VALUES = asList("one", "two", "three", "four", "five");

    private static final String ENTITY_CLASSNAME = "org.motechproject.test.Ent";
    private static final String FIELD_NAME = "cbField";
    private static final String CB_TABLE_NAME = "cbTableName";

    @InjectMocks
    private ComboboxValueHelper cbValueHelper = new ComboboxValueHelper();

    @Mock
    private ComboboxValueRepository cbValueRepository;

    @Mock
    private MetadataHelper metadataHelper;

    @Mock
    private Entity entity;

    @Mock
    private Field field;

    @Mock
    private Type type;

    @Before
    public void setUp() {
        when(entity.getClassName()).thenReturn(ENTITY_CLASSNAME);
        when(field.getName()).thenReturn(FIELD_NAME);
        when(field.getEntity()).thenReturn(entity);
        when(field.getType()).thenReturn(type);
        when(type.isCombobox()).thenReturn(true);
        when(metadataHelper.getComboboxTableName(entity, field)).thenReturn(CB_TABLE_NAME);
    }

    @Test
    public void shouldReturnValuesForCbSingleSelectNoUserSupplied() {
        setUpCb(false, false);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entity, field);

        assertEquals(PREDEFINED_VALUES, result);
        verifyZeroInteractions(cbValueRepository);
    }

    @Test
    public void shouldReturnValuesForCbMultiSelectNoUserSupplied() {
        setUpCb(true, false);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entity, field);

        assertEquals(PREDEFINED_VALUES, result);
        verifyZeroInteractions(cbValueRepository);
    }

    @Test
    public void shouldReturnValuesForCbSingleSelectUserSupplied() {
        setUpCb(false, true);
        when(cbValueRepository.getComboboxValuesForStringField(entity, field))
                .thenReturn(VALUES_FROM_REPOSITORY);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entity, field);

        assertEquals(MERGED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForStringField(entity, field);
        verify(cbValueRepository, never()).getComboboxValuesForCollection(anyString());
    }

    @Test
    public void shouldReturnValuesForCbMultiSelectUserSupplied() {
        setUpCb(true, true);
        when(cbValueRepository.getComboboxValuesForCollection(CB_TABLE_NAME))
                .thenReturn(VALUES_FROM_REPOSITORY);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entity, field);

        assertEquals(MERGED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForCollection(CB_TABLE_NAME);
        verify(cbValueRepository, never()).getComboboxValuesForStringField(any(Entity.class), any(Field.class));
    }

    @Test
    public void shouldNotRethrowExceptionFromRepository() {
        setUpCb(true, true);
        when(cbValueRepository.getComboboxValuesForCollection(CB_TABLE_NAME))
                .thenThrow(new IllegalStateException("An exception from the database"));

        List<String> result = cbValueHelper.getAllValuesForCombobox(entity, field);

        assertEquals(PREDEFINED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForCollection(CB_TABLE_NAME);
        verify(cbValueRepository, never()).getComboboxValuesForStringField(any(Entity.class), any(Field.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullField() {
        cbValueHelper.getAllValuesForCombobox(entity, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullEntity() {
        cbValueHelper.getAllValuesForCombobox(null, field);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullEntityAndField() {
        cbValueHelper.getAllValuesForCombobox(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenFieldIsNotCb() {
        when(type.isCombobox()).thenReturn(false);
        cbValueHelper.getAllValuesForCombobox(null, null);
    }

    private void setUpCb(boolean allowMultiSelection, boolean allowUserSupplied) {
        when(field.getSettings()).thenReturn(asList(predefinedValues(), allowMultiSelectionSetting(allowMultiSelection),
                allowUserSuppliedSetting(allowUserSupplied)));
    }

    private FieldSetting predefinedValues() {
        FieldSetting fieldSetting = new FieldSetting(field, new TypeSetting(Constants.Settings.COMBOBOX_VALUES));
        fieldSetting.setValue(TypeHelper.buildStringFromList(PREDEFINED_VALUES));
        return fieldSetting;
    }

    private FieldSetting allowUserSuppliedSetting(boolean allow) {
        FieldSetting fieldSetting = new FieldSetting(field, new TypeSetting(Constants.Settings.ALLOW_USER_SUPPLIED));
        fieldSetting.setValue(String.valueOf(allow));
        return fieldSetting;
    }

    private FieldSetting allowMultiSelectionSetting(boolean allow) {
        FieldSetting fieldSetting = new FieldSetting(field, new TypeSetting(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS));
        fieldSetting.setValue(String.valueOf(allow));
        return fieldSetting;
    }
}
