package org.motechproject.mds.dto;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.motechproject.mds.exception.MDSValidationException;

public class EntityDtoTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateIfNameExists() {
        EntityDto entityDto = new EntityDto(null, null);

        expectedException.expect(MDSValidationException.class);
        expectedException.expectMessage("mds.validation.error.entityNameNotExists");

        entityDto.validate();
    }

    @Test
    public void shouldValidateIfNameIsAlphaNumeric() {
        EntityDto entityDto = new EntityDto(null, "nonAlphaNumeric%name");

        expectedException.expect(MDSValidationException.class);
        expectedException.expectMessage("mds.validation.error.entityNameNonAlphanumeric");

        entityDto.validate();
    }

    @Test
    public void shouldValidateIfNameIsLessThan30Characters() {
        EntityDto entityDto = new EntityDto(null, "invalidNameWithMoreThan64Characters");

        expectedException.expect(MDSValidationException.class);
        expectedException.expectMessage("mds.validation.error.entityNameLength");

        entityDto.validate();
    }

    @Test
    public void shouldNotThrowExceptionForValidName(){
        EntityDto entityDto = new EntityDto(null, "validName64");

        entityDto.validate();
    }
}
