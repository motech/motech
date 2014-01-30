package org.motechproject.mds.service.impl;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeSettingsMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.ex.TypeAlreadyExistsException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeSettingsMappings;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TypeServiceImplIT extends BaseIT {
    @Autowired
    private AllFieldTypes allFieldTypes;

    @Autowired
    private TypeServiceImpl typeService;

    @Autowired
    private AllTypeSettingsMappings allTypeSettingsMappings;

    @Autowired
    private AllTypeValidationMappings allTypeValidationMappings;

    @Before
    public void setUp() {
        clearDB();
        allFieldTypes.save(new AvailableTypeDto("int",
                new TypeDto("mds.field.integer", "mds.field.description.integer", "java.lang.Integer")));
        allFieldTypes.save(new AvailableTypeDto("bool",
                new TypeDto("mds.field.boolean", "mds.field.description.boolean", "java.lang.Boolean")));
    }

    @After
    public void tearDown() {
        clearDB();
    }

    @Test
    public void shouldRetrieveAvailableTypes() {
        List<AvailableTypeDto> availableTypes = typeService.getAllFieldTypes();

        assertThat(availableTypes.size(), Is.is(2));
        assertTrue(availableTypes.contains(new AvailableTypeDto("int",
                new TypeDto("mds.field.integer", "mds.field.description.integer", "java.lang.Integer"))));
        assertTrue(availableTypes.contains(new AvailableTypeDto("bool",
                new TypeDto("mds.field.boolean", "mds.field.description.boolean", "java.lang.Boolean"))));
    }

    @Test
    public void shouldAddAndPersistNewAvailableType() {
        int count = typeService.getAllFieldTypes().size();

        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(1)));
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(2)));

        typeService.createFieldType(getListOfNewTypes().get(0), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        typeService.createFieldType(getListOfNewTypes().get(1), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(1)));
        typeService.createFieldType(getListOfNewTypes().get(2), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(2)));

        assertThat(typeService.getAllFieldTypes().size(), Is.is(count + 3));
    }

    @Test
    public void shouldAddAndPersistNewAvailableTypeWithGivenValidation() {
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(1)));
        typeService.createFieldType(getListOfNewTypes().get(0), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(0)));

        FieldValidationDto validation = new FieldValidationDto(new ValidationCriterionDto("myCriterion", getListOfNewTypes().get(0).getType()));
        typeService.createFieldType(getListOfNewTypes().get(1), validation, null);

        AvailableFieldTypeMapping typeMapping = allFieldTypes.getByName(getListOfNewTypes().get(1).getType().getDisplayName());
        assertNotNull(typeMapping);
        TypeValidationMapping validationMapping = allTypeValidationMappings.getValidationForType(typeMapping);
        assertNotNull(validationMapping);
        assertThat(validationMapping.getCriteria().size(), Is.is(1));
    }

    @Test
    public void shouldAddAndPersistNewAvailableTypeWithGivenSetting() {
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(1)));
        typeService.createFieldType(getListOfNewTypes().get(0), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(0)));

        SettingDto settingDto = new SettingDto("mySetting", "mySetting", getListOfNewTypes().get(0).getType());
        typeService.createFieldType(getListOfNewTypes().get(1), null, settingDto);

        AvailableFieldTypeMapping typeMapping = allFieldTypes.getByName(getListOfNewTypes().get(1).getType().getDisplayName());
        assertNotNull(typeMapping);
        List<TypeSettingsMapping> typeSettingsMappings = allTypeSettingsMappings.getSettingsForType(typeMapping);
        assertNotNull(typeSettingsMappings);
        assertThat(typeSettingsMappings.size(), Is.is(1));
        assertTrue(typeSettingsMappings.get(0).getName().equals("mySetting"));
    }

    @Test
    public void shouldDeleteFieldType() {
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        SettingDto settingDto = new SettingDto("mySetting", "mySetting", getListOfNewTypes().get(0).getType());
        typeService.createFieldType(getListOfNewTypes().get(0), null, settingDto);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(0)));

        typeService.deleteFieldType(getListOfNewTypes().get(0).getType().getDisplayName());
        assertFalse(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
    }

    @Test(expected = TypeAlreadyExistsException.class)
    public void shouldNotAcceptIdenticalTypes() {
        int count = typeService.getAllFieldTypes().size();
        typeService.createFieldType(getListOfNewTypes().get(0), null, null);
        assertTrue(allFieldTypes.typeExists(getListOfNewTypes().get(0)));
        assertThat(typeService.getAllFieldTypes().size(), Is.is(count + 1));

        typeService.createFieldType(getListOfNewTypes().get(0), null, null);
        assertThat(typeService.getAllFieldTypes().size(), Is.is(count + 1));
    }


    private List<AvailableTypeDto> getListOfNewTypes() {
        AvailableTypeDto newType = new AvailableTypeDto(101L, "myType",
                new TypeDto("test", "test", "org.motechproject.myClass"));
        AvailableTypeDto newType2 = new AvailableTypeDto(102L, "myType2",
                new TypeDto("test2", "test2", "org.motechproject.myClass2"));
        AvailableTypeDto newType3 = new AvailableTypeDto(103L, "myType3",
                new TypeDto("test3", "test3", "org.motechproject.myClass3"));

        List<AvailableTypeDto> availableTypeDtoList = new ArrayList<>();
        availableTypeDtoList.add(newType);
        availableTypeDtoList.add(newType2);
        availableTypeDtoList.add(newType3);

        return availableTypeDtoList;
    }
}
