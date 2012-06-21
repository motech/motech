package org.motechproject.mobileforms.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.mobileforms.api.validator.TestFormBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;

public class FormGroupValidatorTest {

    private FormGroupValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new FormGroupValidator();
    }

    @Test
    public void shouldValidateFormsAsPerTheSetPriority() {
        final TestFormBean formBean1 = new TestFormBean("study", "form1", "<xml>xml</xml>", "validator1", "formType", Collections.<String>emptyList(), "fName1", "lName1");
        final TestFormBean formBean2 = new TestFormBean("study", "form2", "<xml>xml</xml>", "validator2", "formType", Arrays.asList("form1", "form3"), "fName2", "lName2");
        final TestFormBean formBean3 = new TestFormBean("study", "form3", "<xml>xml</xml>", "validator3", "formType", Collections.<String>emptyList(), "fName3", "lName3");

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean1, formBean2, formBean3);
        FormBeanGroup formGroup = new FormBeanGroup(formBeans);

        final FormValidator formValidator1 = mock(FormValidator.class);
        final FormValidator formValidator2 = mock(FormValidator.class);
        final FormValidator formValidator3 = mock(FormValidator.class);

        final List<String> methodCalls = new ArrayList<String>();

        mockValidatorToPass(formBean1, formValidator1, methodCalls);
        mockValidatorToPass(formBean2, formValidator2, methodCalls);
        mockValidatorToPass(formBean3, formValidator3, methodCalls);

        Map<String, FormValidator> validators = new HashMap<String, FormValidator>() {{
            put("validator1", formValidator1);
            put("validator2", formValidator2);
            put("validator3", formValidator3);
        }};

        validator.validate(formGroup, validators, formBeans);

        assertTrue(methodCalls.indexOf("form2") > methodCalls.indexOf("form1"));
        assertTrue(methodCalls.indexOf("form2") > methodCalls.indexOf("form3"));
    }

    @Test
    public void shouldFailAFormIfADependentFormHasValidationErrors() {
        final TestFormBean formBean1 = new TestFormBean("study", "form1", "<xml>xml</xml>", "validator1", "formType", Collections.<String>emptyList(), "fName1", "lName1");
        final TestFormBean formBean2 = new TestFormBean("study", "form2", "<xml>xml</xml>", "validator2", "formType", Arrays.asList("form1"), "fName2", "lName2");
        final TestFormBean formBean3 = new TestFormBean("study", "form3", "<xml>xml</xml>", "validator3", "formType", Collections.<String>emptyList(), "fName3", "lName3");
        final TestFormBean formBean4 = new TestFormBean("study", "form4", "<xml>xml</xml>", "validator4", "formType", Arrays.asList("form2"), "fName4", "lName4");

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean1, formBean2, formBean3, formBean4);
        FormBeanGroup formGroup = new FormBeanGroup(formBeans);

        final FormValidator formValidator1 = mock(FormValidator.class);
        final FormValidator formValidator2 = mock(FormValidator.class);
        final FormValidator formValidator3 = mock(FormValidator.class);
        final FormValidator formValidator4 = mock(FormValidator.class);

        final List<String> methodCalls = new ArrayList<String>();

        mockValidatorToFail(formBean1, formValidator1, methodCalls, Arrays.asList(new FormError("parameter", "error")));
        mockValidatorToFail(formBean2, formValidator2, methodCalls, Arrays.asList(new FormError("parameter", "error")));
        mockValidatorToPass(formBean3, formValidator3, methodCalls);
        mockValidatorToPass(formBean4, formValidator4, methodCalls);

        Map<String, FormValidator> validators = new HashMap<String, FormValidator>() {{
            put("validator1", formValidator1);
            put("validator2", formValidator2);
            put("validator3", formValidator3);
            put("validator4", formValidator4);
        }};

        validator.validate(formGroup, validators, formBeans);

        assertThat(formBean1.getFormErrors(), is(equalTo(Arrays.asList(new FormError("parameter", "error")))));
        assertThat(formBean2.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form1", "Dependent form failed")))));
        assertThat(formBean3.getFormErrors(), is(equalTo(Collections.<FormError>emptyList())));
        assertThat(formBean4.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form2", "Dependent form failed")))));

        resetFormBeans(formBean1, formBean2, formBean3, formBean4);

        mockValidatorToPass(formBean1, formValidator1, methodCalls);
        mockValidatorToFail(formBean2, formValidator2, methodCalls, Arrays.asList(new FormError("parameter", "error")));
        mockValidatorToPass(formBean3, formValidator3, methodCalls);
        mockValidatorToPass(formBean4, formValidator4, methodCalls);

        validator.validate(formGroup, validators, formBeans);

        assertThat(formBean1.getFormErrors(), is(equalTo(Collections.<FormError>emptyList())));
        assertThat(formBean2.getFormErrors(), is(equalTo(Arrays.asList(new FormError("parameter", "error")))));
        assertThat(formBean3.getFormErrors(), is(equalTo(Collections.<FormError>emptyList())));
        assertThat(formBean4.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form2", "Dependent form failed")))));
    }

    @Test
    public void shouldMarkTheFormAsFailedIfItsValidatorThrowsARuntimeException() {
        final TestFormBean formBean1 = new TestFormBean("study", "form1", "<xml>xml</xml>", "validator1", "formType", Collections.<String>emptyList(), "fName1", "lName1");
        final TestFormBean formBean2 = new TestFormBean("study", "form2", "<xml>xml</xml>", "validator2", "formType", Arrays.asList("form1"), "fName2", "lName2");

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean1, formBean2);
        FormBeanGroup formGroup = new FormBeanGroup(formBeans);

        final FormValidator formValidator1 = mock(FormValidator.class);
        final FormValidator formValidator2 = mock(FormValidator.class);

        final List<String> methodCalls = new ArrayList<String>();

        Map<String, FormValidator> validators = new HashMap<String, FormValidator>() {{
            put("validator1", formValidator1);
            put("validator2", formValidator2);
        }};

        mockValidatorToPass(formBean1, formValidator1, methodCalls);
        doThrow(new RuntimeException()).when(formValidator2).validate(formBean2, formGroup, formBeans);

        validator.validate(formGroup, validators, formBeans);

        assertThat(formBean1.getFormErrors().size(), is(equalTo(0)));
        assertThat(formBean2.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form2", "Server exception, contact your administrator")))));
    }

    @Test
    public void shouldMarkAllFormsAsFailedIfThereIsARunTimeExceptionIsThrownBeforeProcessingTheForms() {
        final TestFormBean formBean1 = new TestFormBean("study", "form1", "<xml>xml</xml>", "validator1", "formType", Collections.<String>emptyList(), "fName1", "lName1");
        final TestFormBean formBean2 = new TestFormBean("study", "form2", "<xml>xml</xml>", "validator2", "formType", Arrays.asList("form1"), "fName2", "lName2");

        List<FormBean> formBeans = Arrays.<FormBean>asList(formBean1, formBean2);
        FormBeanGroup formGroup = spy(new FormBeanGroup(formBeans));
        doThrow(new RuntimeException()).when(formGroup).sortByDependency();

        Map<String, FormValidator> validators = new HashMap<String, FormValidator>();

        validator.validate(formGroup, validators, formBeans);

        assertThat(formBean1.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form1", "Server exception, contact your administrator")))));
        assertThat(formBean2.getFormErrors(), is(equalTo(Arrays.asList(new FormError("Form Error:form2", "Server exception, contact your administrator")))));
    }

    private void resetFormBeans(TestFormBean... formBeans) {
        for (TestFormBean formBean : formBeans) {
            formBean.clearFormErrors();
        }
    }

    private void mockValidatorToPass(TestFormBean formBean, FormValidator formValidator, final List<String> methodCalls) {
        mockValidator(formBean, formValidator, methodCalls, Collections.<FormError>emptyList());
    }

    private void mockValidatorToFail(TestFormBean formBean, FormValidator formValidator, final List<String> methodCalls, List<FormError> errorsToBeReturned) {
        mockValidator(formBean, formValidator, methodCalls, errorsToBeReturned);
    }

    private void mockValidator(TestFormBean formBean, FormValidator formValidator, final List<String> methodCalls, final List<FormError> errorsToBeReturned) {
        reset(formValidator);
        doAnswer(new Answer<List>() {
            @Override
            public List answer(InvocationOnMock invocationOnMock) throws Throwable {
                methodCalls.add(((FormBean) invocationOnMock.getArguments()[0]).getFormname());
                return errorsToBeReturned;
            }
        }).when(formValidator).validate(eq(formBean), Matchers.<FormBeanGroup>any(), anyList());
    }
}
