package org.motechproject.server.web.validator;

import org.junit.Test;
import org.springframework.validation.Errors;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequiredFieldValidatorTest {

    @Test
    public void shouldRejectIfFieldIsNull() {
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("name");

        Errors errors = mock(Errors.class);
        when(errors.getFieldValue("name")).thenReturn(null);

        Object target = new Object();
        fieldValidator.validate(target, errors);

        verify(errors).getFieldValue("name");
        verify(errors).rejectValue("name", String.format(RequiredFieldValidator.ERROR_REQUIRED, "name"), null, null);
    }

    @Test
    public void shouldBeConsideredEqualIfFieldNameIsEqual() {
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("name");
        RequiredFieldValidator equalFieldValidator = new RequiredFieldValidator("name");
        RequiredFieldValidator differentFieldValidator = new RequiredFieldValidator("Name");
        RequiredFieldValidator anotherDifferentFieldValidator = new RequiredFieldValidator("someOtherField");
        assertTrue(fieldValidator.equals(fieldValidator));
        assertTrue(fieldValidator.equals(equalFieldValidator));
        assertFalse(fieldValidator.equals(differentFieldValidator));
        assertFalse(fieldValidator.equals(anotherDifferentFieldValidator));
    }
}
