package org.motechproject.mobileforms.api.validator;

import org.junit.Test;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
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
        TestFormBeanWithInvalidValidatorAnnotation formBean = new TestFormBeanWithInvalidValidatorAnnotation("FamilyName");
        assertThat(new TestFormValidator().validate(formBean, new FormBeanGroup(Arrays.<FormBean>asList(formBean)), Arrays.<FormBean>asList(formBean)), hasItem(new FormError("familyName", "Server exception, contact your administrator")));
    }

    @Test
    public void shouldReturnNoErrorsIfAllFieldsAreValid() {
        final TestFormBean formBean = new TestFormBean().setFirstName("First Name");
        assertThat(new TestFormValidator().validate(formBean, new FormBeanGroup(Arrays.<FormBean>asList(formBean)), Arrays.<FormBean>asList(formBean)), is(equalTo(Collections.<FormError>emptyList())));
    }

    @Test
    public void shouldCheckIfAttributesAnnotatedAsRequiredHaveAValue() {
        final TestFormBean formBean = new TestFormBean();
        assertThat(new TestFormValidator().validate(formBean, new FormBeanGroup(Arrays.<FormBean>asList(formBean)), Arrays.<FormBean>asList(formBean)), hasItem(new FormError("firstName", "is mandatory")));
    }

    @Test
    public void shouldCheckIfAttributesAnnotatedWithRegularExpressionGetValidatedAgainstThePattern() {
        final TestFormBean formBean = new TestFormBean().setFirstName("First 1 name");
        assertThat(new TestFormValidator().validate(formBean, new FormBeanGroup(Arrays.<FormBean>asList(formBean)), Arrays.<FormBean>asList(formBean)), is(equalTo(Arrays.asList(new FormError("firstName", "wrong format")))));
    }

    @Test
    public void shouldCheckIfAttributesAnnotatedWithMaxLengthGetValidatedAgainstMaxAllowedLength() {
        final TestFormBean bean = new TestFormBean().setFirstName("very looooooooooonnnnnnnnnngggggggg name");
        assertThat(new TestFormValidator().validate(bean, new FormBeanGroup(Arrays.<FormBean>asList(bean)), Arrays.<FormBean>asList(bean)), hasItem(new FormError("firstName", "too long")));
    }
}



