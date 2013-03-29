package org.motechproject.mobileforms.api.web;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.callbacks.FormGroupPublisher;
import org.motechproject.mobileforms.api.callbacks.FormParser;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.domain.FormOutput;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.service.UsersService;
import org.motechproject.mobileforms.api.validator.FormValidator;
import org.motechproject.mobileforms.api.validator.TestFormBean;
import org.motechproject.mobileforms.api.vo.Study;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.ServletContext;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormUploadServletTest {

    private FormUploadServlet formUploadServlet;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private MobileFormsService mobileFormsService;
    @Mock
    private UsersService usersService;
    @Mock
    private EpihandyXformSerializer epihandySerializer;
    @Mock
    private FormParser formParser;
    @Mock
    private FormGroupPublisher formGroupPublisher;
    @Mock
    private ServletContext mockServletContext;
    @Mock
    private FormOutput formOutput;
    @Mock
    private Properties mobileFormsProperties;

    private Integer groupIndex = 2;

    @Before
    public void setup() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        when(applicationContext.getBean("mobileFormsProperties", Properties.class)).thenReturn(mobileFormsProperties);
        formUploadServlet = spy(new FormUploadServlet(applicationContext));
        doReturn(mockServletContext).when(formUploadServlet).getServletContext();
        doReturn(formParser).when(formUploadServlet).createFormProcessor();
        ReflectionTestUtils.setField(formUploadServlet, "mobileFormsService", mobileFormsService);
        ReflectionTestUtils.setField(formUploadServlet, "usersService", usersService);
        ReflectionTestUtils.setField(formUploadServlet, "formGroupPublisher", formGroupPublisher);
        doReturn(epihandySerializer).when(formUploadServlet).serializer();
        doReturn(formOutput).when(formUploadServlet).getFormOutput();
    }

    @Test
    public void shouldProcessUploadedForms() throws Exception {
        final String validatorClass = "org.motechproject.mobileforms.api.validator.TestANCVisitFormValidator";
        final FormValidator formValidator = mock(FormValidator.class);
        FormBean successForm = new TestFormBean("study", "form1", "xml", validatorClass, "type", Collections.<String>emptyList(), "group1", "last");
        FormBean failureForm = new TestFormBean("study", "form2", "xml", validatorClass, "type", Collections.<String>emptyList(), "group1", "last");
        FormBean formInDifferentGroup = new TestFormBean("study", "form3", "xml", validatorClass, "type", Collections.<String>emptyList(), "group2", "last");

        List<FormBean> formBeans = Arrays.asList(successForm, failureForm, formInDifferentGroup);
        List<FormError> formErrors = Arrays.asList(new FormError("field_name", "error"));
        Map<Integer, String> formIdMap = new HashMap<Integer, String>();
        Study study = new Study("study", formBeans);

        when(formParser.getStudies()).thenReturn(Arrays.asList(study));

        mockServletContextToReturnValidators(new HashMap<String, FormValidator>() {{
            put(validatorClass, formValidator);
        }});

        final FormBeanGroup groupOne = new FormBeanGroup(Arrays.asList(successForm, failureForm));
        FormBeanGroup groupTwo = new FormBeanGroup(Arrays.asList(formInDifferentGroup));
        when(formValidator.validate(successForm, groupOne, formBeans)).thenReturn(Collections.EMPTY_LIST);
        when(formValidator.validate(failureForm, groupOne, formBeans)).thenReturn(formErrors);
        when(formValidator.validate(formInDifferentGroup, groupTwo, formBeans)).thenReturn(Collections.EMPTY_LIST);

        when(mobileFormsService.getFormIdMap()).thenReturn(formIdMap);

        populateHttpRequest(request, "username", "password", groupIndex);

        formUploadServlet.doPost(request, response);

        verify(formOutput).addStudy(study);

        assertThat(failureForm.getFormErrors(), is(equalTo(formErrors)));

        verify(formOutput).writeFormErrors(any(DataOutputStream.class));

        ArgumentCaptor<FormBeanGroup> publishCaptor = ArgumentCaptor.forClass(FormBeanGroup.class);
        ArgumentCaptor<FormBeanGroup> logCaptor = ArgumentCaptor.forClass(FormBeanGroup.class);
        verify(formGroupPublisher,times(2)).publishFormsForLogging(logCaptor.capture());
        verify(formGroupPublisher, times(2)).publish(publishCaptor.capture());
        assertThat(publishCaptor.getAllValues(), is(equalTo(Arrays.asList(new FormBeanGroup(Arrays.asList(successForm)), new FormBeanGroup(Arrays.asList(formInDifferentGroup))))));
        assertThat(logCaptor.getAllValues(), is(equalTo(Arrays.asList(new FormBeanGroup(Arrays.asList(successForm,failureForm)), new FormBeanGroup(Arrays.asList(formInDifferentGroup))))));

        verify(epihandySerializer).addDeserializationListener(formParser);
        verify(epihandySerializer).deserializeStudiesWithEvents(any(DataInputStream.class), eq(formIdMap));

    }

    private void mockServletContextToReturnValidators(Map<String, FormValidator> validators) {
        reset(mockServletContext);
        when(mockServletContext.getAttributeNames()).thenReturn(Collections.enumeration(validators.keySet()));
        for (Map.Entry<String, FormValidator> entry : validators.entrySet()) {
            when(mockServletContext.getAttribute(entry.getKey())).thenReturn(entry.getValue());
        }
    }

    private void populateHttpRequest(MockHttpServletRequest request, String userName, String password, Integer groupIndex)
            throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteStream);
        dataOutputStream.writeUTF(userName);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF("epihandyser");
        dataOutputStream.writeUTF("en");
        if (groupIndex != null) dataOutputStream.writeInt(groupIndex);
        request.setContent(byteStream.toByteArray());
    }

    @Test
    public void shouldReturnListOfValidatorInServletContext() {
        when(mockServletContext.getAttributeNames()).thenReturn(Collections.enumeration(Arrays.asList("validator1", "validator2", "some_attribute_name")));
        final FormValidator formValidator1 = mock(FormValidator.class);
        final FormValidator formValidator2 = mock(FormValidator.class);

        when(mockServletContext.getAttribute("validator1")).thenReturn(formValidator1);
        when(mockServletContext.getAttribute("validator2")).thenReturn(formValidator2);

        final Map<String, FormValidator> formValidators = formUploadServlet.getFormValidators();
        Map<String, FormValidator> expected = new HashMap<String, FormValidator>() {{
            put("validator1", formValidator1);
            put("validator2", formValidator2);
        }};
        assertThat(formValidators, is(equalTo(expected)));
    }
}
