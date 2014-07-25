package org.motechproject.server.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.web.validator.ValidationUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.motechproject.server.web.BootstrapConfigFormValidator.ERROR_REQUIRED;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ValidationUtils.class)
public class BootstrapConfigFormValidatorTest {

    @Mock
    private Errors errors;

    private BootstrapConfigFormValidator bootstrapConfigFormValidator = new BootstrapConfigFormValidator();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldValidateOnlyForEmptyFields() {
        bootstrapConfigFormValidator.validate(new BootstrapConfigForm(), errors);

        verifyStatic();
        ValidationUtils.validateEmptyOrWhitespace(errors, ERROR_REQUIRED, "sqlUrl");
    }

    @Test
    public void shouldValidateForConfigSource(){
        when(errors.hasFieldErrors("sqlUrl")).thenReturn(false);
        when(errors.getFieldValue("sqlbUrl")).thenReturn("jdbc://localhost");

        BootstrapConfigForm bootstrapConfigForm = new BootstrapConfigForm();
        bootstrapConfigForm.setConfigSource("invalid");

        bootstrapConfigFormValidator.validate(bootstrapConfigForm, errors);

        verify(errors).rejectValue("invalid","server.error.invalid.configSource");
    }
}
