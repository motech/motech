package org.motechproject.security.validator.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.security.exception.PasswordTooShortException;
import org.motechproject.security.exception.PasswordValidatorException;
import org.motechproject.security.validator.PasswordValidator;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MinLengthValidatorDecoratorTest {

    @Mock
    private PasswordValidator validator;

    @Test
    public void shouldAcceptValidLength() {
        PasswordValidator decoratedValidator = new MinLengthValidatorDecorator(validator, 3);
        decoratedValidator.validate("pass");
        verify(validator).validate("pass");

        decoratedValidator = new MinLengthValidatorDecorator(validator, 0);
        decoratedValidator.validate("p");
        verify(validator).validate("pass");
    }

    @Test(expected = PasswordTooShortException.class)
    public void shouldValidateLength() {
        PasswordValidator decoratedValidator = new MinLengthValidatorDecorator(validator, 3);
        decoratedValidator.validate("eh");
    }

    @Test(expected = PasswordValidatorException.class)
    public void shouldThrowExceptionFromInnerValidator() {
        PasswordValidator decoratedValidator = new MinLengthValidatorDecorator(validator, 3);
        doThrow(new PasswordValidatorException("wrong")).when(validator).validate("password");
        decoratedValidator.validate("password");
    }
}
