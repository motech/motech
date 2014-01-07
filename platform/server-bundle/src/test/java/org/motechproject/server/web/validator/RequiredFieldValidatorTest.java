package org.motechproject.server.web.validator;

import org.junit.Test;
import org.motechproject.server.web.form.StartupForm;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class RequiredFieldValidatorTest {

    @Test
    public void shouldRejectIfFieldIsNull() {
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("name", null);

        List<String> errors = new ArrayList<>();
        fieldValidator.validate(new StartupForm(), errors);

        assertTrue(errors.contains(String.format(RequiredFieldValidator.ERROR_REQUIRED, "name")));
    }

    @Test
    public void shouldBeConsideredEqualIfFieldNameIsEqual() {
        RequiredFieldValidator fieldValidator = new RequiredFieldValidator("name", "");
        RequiredFieldValidator equalFieldValidator = new RequiredFieldValidator("name", "");
        RequiredFieldValidator differentFieldValidator = new RequiredFieldValidator("Name", "");
        RequiredFieldValidator anotherDifferentFieldValidator = new RequiredFieldValidator("someOtherField", "");

        assertTrue(fieldValidator.equals(fieldValidator));
        assertTrue(fieldValidator.equals(equalFieldValidator));
        assertFalse(fieldValidator.equals(differentFieldValidator));
        assertFalse(fieldValidator.equals(anotherDifferentFieldValidator));
    }
}
