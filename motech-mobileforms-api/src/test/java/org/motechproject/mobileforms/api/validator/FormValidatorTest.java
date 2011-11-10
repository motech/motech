package org.motechproject.mobileforms.api.validator;

import org.junit.Test;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FormValidatorTest {

    @Test
    public void shouldReturnErrorIfInvalidValidationAnnotationHasBeenConfiguredToTheFormBeanField() {
        FormBean formBeanWithInvalidValidator = new FormBean() {
            @InvalidValidationMarker
            private String familyName;

            public String getFamilyName() {
                return familyName;
            }
        };
        assertThat(new TestFormValidator().validate(formBeanWithInvalidValidator), hasItem(new FormError("familyName", "Server exception, contact your administrator")));
    }

    @Test
    public void shouldReturnNoErrorsIfAllFieldsAreValid() {
        assertThat(new TestFormValidator().validate(new TestFormBean().setFirstName("First Name")), is(equalTo(Collections.<FormError>emptyList())));
    }

    @Test
    public void shouldCheckIfAttributesAnnotatedAsRequiredHaveAValue() {
        assertThat(new TestFormValidator().validate(new TestFormBean()), hasItem(new FormError("firstName", "firstName is mandatory")));
    }

    @Test
    public void shouldCheckIfAttributesAnnotatedWithRegularExpressionGetValidatedAgainstThePattern() {
        assertThat(new TestFormValidator().validate(new TestFormBean().setFirstName("First 1 name")), is(equalTo(Arrays.asList(new FormError("firstName", "firstName is invalid")))));
    }
}



