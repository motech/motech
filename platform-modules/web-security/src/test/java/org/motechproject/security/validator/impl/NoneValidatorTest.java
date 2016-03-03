package org.motechproject.security.validator.impl;

import org.junit.Test;

public class NoneValidatorTest {

    private NoneValidator noneValidator = new NoneValidator(null);

    @Test
    public void shouldLetEverythingPass() {
        noneValidator.validate("pass");
        noneValidator.validate("123%mmmAA");
        noneValidator.validate("");
        noneValidator.validate(null);
        noneValidator.validate("a");
        noneValidator.validate("B");
        noneValidator.validate("3");
        noneValidator.validate("!");
    }
}
