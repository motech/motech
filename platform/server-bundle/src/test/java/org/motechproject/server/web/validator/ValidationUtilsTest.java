package org.motechproject.server.web.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationUtilsTest {
    @Mock
    Errors errors;

    @Test
    public void shouldValidateForUrl() throws Exception {
        when(errors.getFieldValue("dbUrl")).thenReturn("invalidurl");

        ValidationUtils.validateUrl(errors, "dbUrl");

        verify(errors).rejectValue("dbUrl", "server.error.invalid.dbUrl");
    }

    @Test
    public void shouldValidateForLocalhostUrl() throws Exception {
        when(errors.getFieldValue("dbUrl")).thenReturn("http://localhost");

        ValidationUtils.validateUrl(errors, "dbUrl");

        verify(errors, never()).rejectValue(anyString(), anyString());
    }

    @Test
    public void shouldValidateIfDbUrlDbUsernameAndDbPasswordIsEmpty() throws Exception {
        when(errors.getFieldValue("field1")).thenReturn("");
        when(errors.getFieldValue("field2")).thenReturn("   ");
        when(errors.getFieldValue("field3")).thenReturn(null);
        when(errors.getFieldValue("field4")).thenReturn("some value");

        ValidationUtils.validateEmptyOrWhitespace(errors, "field_in_error_%s", "field1", "field2", "field3");

        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(errors, times(3)).rejectValue(fieldCaptor.capture(), messageCaptor.capture(), aryEq((Object[]) null), anyString() );

        List<String> rejectedFields = fieldCaptor.getAllValues();
        assertThat(rejectedFields.size(), is(3));
        assertThat(rejectedFields.get(0), is("field1"));
        assertThat(rejectedFields.get(1), is("field2"));
        assertThat(rejectedFields.get(2), is("field3"));

        List<String> errorMessages = messageCaptor.getAllValues();
        assertThat(errorMessages.size(), is(3));
        assertThat(errorMessages.get(0), is("field_in_error_field1"));
        assertThat(errorMessages.get(1), is("field_in_error_field2"));
        assertThat(errorMessages.get(2), is("field_in_error_field3"));
    }

}
