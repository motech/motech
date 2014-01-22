package org.motechproject.mds.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.BaseIT;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.TypeValidationAlreadyExistsException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.motechproject.mds.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jdo.PersistenceManager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ValidationServiceIT extends BaseIT {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AllFieldTypes allFieldTypes;

    @Autowired
    private AllTypeValidationMappings allTypeValidationMappings;

    private static final String TYPE_NAME = "newType";

    @Before
    public void setUp() throws Exception {
        PersistenceManager persistenceManager = getPersistenceManager();
        persistenceManager.makePersistent(new AvailableFieldTypeMapping(101L, "myNewType", new TypeDto(TYPE_NAME, "test", "org.motechproject.myClass")));
    }

    @After
    public void tearDown() {
        clearDB();
    }

    @Test
    public void shouldSaveValidationForType() {
        assertNotNull(allFieldTypes.getByName(TYPE_NAME));

        AvailableFieldTypeMapping typeMapping = allFieldTypes.getByName(TYPE_NAME);
        assertNotNull(typeMapping);
        validationService.saveValidationForType(typeMapping, new FieldValidationDto());
        assertNotNull(allTypeValidationMappings.getValidationForType(typeMapping));
    }

    @Test
    public void shouldDeleteValidationForType() {
        assertNotNull(allFieldTypes.getByName(TYPE_NAME));

        AvailableFieldTypeMapping typeMapping = allFieldTypes.getByName(TYPE_NAME);
        assertNotNull(typeMapping);
        validationService.saveValidationForType(typeMapping, new FieldValidationDto());
        assertNotNull(allTypeValidationMappings.getValidationForType(typeMapping));

        validationService.deleteValidationForType(typeMapping);
        assertNull(allTypeValidationMappings.getValidationForType(typeMapping));
    }

    @Test(expected = TypeValidationAlreadyExistsException.class)
    public void shouldThrowExceptionWhenAddingSecondValidationForGivenType() {
        assertNotNull(allFieldTypes.getByName(TYPE_NAME));

        AvailableFieldTypeMapping typeMapping = allFieldTypes.getByName(TYPE_NAME);
        assertNotNull(typeMapping);
        validationService.saveValidationForType(typeMapping, new FieldValidationDto());
        validationService.saveValidationForType(typeMapping, new FieldValidationDto());
    }
}
