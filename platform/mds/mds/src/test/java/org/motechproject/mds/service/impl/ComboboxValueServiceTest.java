package org.motechproject.mds.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.repository.ComboboxValueRepository;
import org.motechproject.mds.service.MetadataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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
public class ComboboxValueServiceTest {

    private static final List<String> PREDEFINED_VALUES = asList("one", "two", "three");
    private static final List<String> VALUES_FROM_REPOSITORY = asList("one", "two", "four", "five");
    private static final List<String> MERGED_VALUES = asList("one", "two", "three", "four", "five");

    private static final String ENTITY_CLASSNAME = "org.motechproject.test.Ent";
    private static final String FIELD_NAME = "cbField";
    private static final String CB_TABLE_NAME = "cbTableName";

    @InjectMocks
    private ComboboxValueServiceImpl cbValueHelper = new ComboboxValueServiceImpl();

    @Mock
    private ComboboxValueRepository cbValueRepository;

    @Mock
    private MetadataServiceImpl metadataService;

    @Mock
    private EntityDto entityDto;

    @Mock
    private FieldDto fieldDto;

    @Mock
    private TypeDto typeDto;

    @Mock
    FieldBasicDto fieldBasicDto;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference<MetadataService> ref;

    @Before
    public void setUp() {
        when(entityDto.getClassName()).thenReturn(ENTITY_CLASSNAME);
        when(fieldBasicDto.getName()).thenReturn(FIELD_NAME);
        when(fieldDto.getBasic()).thenReturn(fieldBasicDto);
        when(fieldDto.getType()).thenReturn(typeDto);
        when(typeDto.isCombobox()).thenReturn(true);
        when(metadataService.getComboboxTableName(ENTITY_CLASSNAME, FIELD_NAME)).thenReturn(CB_TABLE_NAME);

        when(bundleContext.getServiceReference(MetadataService.class)).thenReturn(ref);
        when(bundleContext.getService(ref)).thenReturn(metadataService);
    }

    @Test
    public void shouldReturnValuesForCbSingleSelectNoUserSupplied() {
        setUpCb(false, false);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entityDto, fieldDto);

        assertEquals(PREDEFINED_VALUES, result);
        verifyZeroInteractions(cbValueRepository);
    }

    @Test
    public void shouldReturnValuesForCbMultiSelectNoUserSupplied() {
        setUpCb(true, false);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entityDto, fieldDto);

        assertEquals(PREDEFINED_VALUES, result);
        verifyZeroInteractions(cbValueRepository);
    }

    @Test
    public void shouldReturnValuesForCbSingleSelectUserSupplied() {
        setUpCb(false, true);
        when(cbValueRepository.getComboboxValuesForStringField(entityDto, fieldDto))
                .thenReturn(VALUES_FROM_REPOSITORY);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entityDto, fieldDto);

        assertEquals(MERGED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForStringField(entityDto, fieldDto);
        verify(cbValueRepository, never()).getComboboxValuesForCollection(anyString());
    }

    @Test
    public void shouldReturnValuesForCbMultiSelectUserSupplied() {
        setUpCb(true, true);
        when(cbValueRepository.getComboboxValuesForCollection(CB_TABLE_NAME))
                .thenReturn(VALUES_FROM_REPOSITORY);

        List<String> result = cbValueHelper.getAllValuesForCombobox(entityDto, fieldDto);

        assertEquals(MERGED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForCollection(CB_TABLE_NAME);
        verify(cbValueRepository, never()).getComboboxValuesForStringField(any(EntityDto.class), any(FieldDto.class));
    }

    @Test
    public void shouldNotReThrowExceptionFromRepository() {
        setUpCb(true, true);
        when(cbValueRepository.getComboboxValuesForCollection(CB_TABLE_NAME))
                .thenThrow(new IllegalStateException("An exception from the database"));

        List<String> result = cbValueHelper.getAllValuesForCombobox(entityDto, fieldDto);

        assertEquals(PREDEFINED_VALUES, result);
        verify(cbValueRepository).getComboboxValuesForCollection(CB_TABLE_NAME);
        verify(cbValueRepository, never()).getComboboxValuesForStringField(any(EntityDto.class), any(FieldDto.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullField() {
        cbValueHelper.getAllValuesForCombobox(entityDto, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullEntity() {
        cbValueHelper.getAllValuesForCombobox(null, fieldDto);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForNullEntityAndField() {
        cbValueHelper.getAllValuesForCombobox((EntityDto) null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenFieldIsNotCb() {
        when(typeDto.isCombobox()).thenReturn(false);
        cbValueHelper.getAllValuesForCombobox((EntityDto) null, null);
    }

    private void setUpCb(boolean allowMultiSelection, boolean allowUserSupplied) {
        when(fieldDto.getSettings()).thenReturn(asList(predefinedValues(), allowMultiSelectionSetting(allowMultiSelection),
                allowUserSuppliedSetting(allowUserSupplied)));
    }

    private SettingDto predefinedValues() {
        return new SettingDto(Constants.Settings.COMBOBOX_VALUES, TypeHelper.buildStringFromList(PREDEFINED_VALUES), typeDto);
    }

    private SettingDto allowUserSuppliedSetting(boolean allow) {
        return new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, String.valueOf(allow), typeDto);
    }

    private SettingDto allowMultiSelectionSetting(boolean allow) {
        return new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, String.valueOf(allow), typeDto);
    }
}
