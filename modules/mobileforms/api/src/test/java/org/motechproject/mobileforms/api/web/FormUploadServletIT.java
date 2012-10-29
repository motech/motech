package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZInputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.callbacks.FormGroupPublisher;
import org.motechproject.mobileforms.api.callbacks.FormParser;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormBeanGroup;
import org.motechproject.mobileforms.api.validator.TestFormBean;
import org.motechproject.mobileforms.api.validator.TestFormValidator;
import org.motechproject.mobileforms.api.vo.Study;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormUploadServletIT {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockServletContext servletContext;
    @Mock
    FormGroupPublisher formGroupPublisher;
    @Mock
    private FormParser formParser;

    @Before
    public void setUp() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();
    }

    @Test
    public void shouldProcessUploadedFormAndReturnValidationErrorsIfErrorsAreFound() throws Exception {
        FormUploadServlet formUploadServlet = new FormUploadServlet();
        ReflectionTestUtils.setField(formUploadServlet, "formGroupPublisher", formGroupPublisher);
        FormUploadServlet servlet = spy(formUploadServlet);
        doReturn(formParser).when(servlet).createFormProcessor();

        final TestFormBean formBeanWithOutError = new TestFormBean("study", "form1", "<xml>xml</xml>", TestFormValidator.class.getName(), "type", Collections.<String>emptyList(), "Abc", null);
        final TestFormBean formBeanWithError = new TestFormBean("study", "form2", "<xml>xml</xml>", TestFormValidator.class.getName(), "type", Collections.<String>emptyList(), "1Abc", null);
        List<FormBean> formBeans = Arrays.<FormBean>asList(formBeanWithOutError, formBeanWithError);

        Study study = new Study("study_name", formBeans);
        List<Study> studies = Arrays.asList(study);
        when(formParser.getStudies()).thenReturn(studies);

        EpihandyXformSerializer epihandyXformSerializer = spy(new EpihandyXformSerializer());
        doNothing().when(epihandyXformSerializer).deserializeStudiesWithEvents(any(DataInputStream.class), anyObject());

        doReturn(epihandyXformSerializer).when(servlet).serializer();

        TestFormValidator testFormValidator = new TestFormValidator();
        servletContext.setAttribute(TestFormValidator.class.getName(), testFormValidator);
        doReturn(servletContext).when(servlet).getServletContext();


        try {
            setupRequestWithActionAndOtherRequestParameters(request, "username", "password", FormDownloadServlet.ACTION_DOWNLOAD_STUDY_LIST);
            servlet.doPost(request, response);
            DataInputStream responseSentToMobile = readResponse(response);
            int expectedNoOfUploadedForms = formBeans.size();
            int expectedNoOfFailedForms = 1;
            int expectedStudyIndex = 0;
            int expectedFormIndex = 1;
            assertThat(responseSentToMobile.readByte(), is(equalTo(FormDownloadServlet.RESPONSE_SUCCESS)));
            assertThat(responseSentToMobile.readInt(), is(equalTo(expectedNoOfUploadedForms)));
            assertThat(responseSentToMobile.readInt(), is(equalTo(expectedNoOfFailedForms)));
            assertThat(responseSentToMobile.readByte(), is(equalTo((byte) expectedStudyIndex)));
            assertThat(responseSentToMobile.readShort(), is(equalTo((short) expectedFormIndex)));
            assertThat(responseSentToMobile.readUTF(), is(equalTo("Errors:firstName=wrong format")));

        } catch (Exception e) {
            assertFalse(true);
        }

        verify(formGroupPublisher).publishFormsForLogging(new FormBeanGroup(Arrays.<FormBean>asList(formBeanWithError)));
        verify(formGroupPublisher).publish(new FormBeanGroup(Arrays.<FormBean>asList(formBeanWithOutError)));
    }

    private DataInputStream readResponse(MockHttpServletResponse response) {
        return new DataInputStream(new ZInputStream(new ByteArrayInputStream(response.getContentAsByteArray())));
    }

    private void setupRequestWithActionAndOtherRequestParameters(MockHttpServletRequest request, String userName, String password, byte actionCode) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF(userName);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF("epihandyser");
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeByte(actionCode);
        request.setContent(byteArrayOutputStream.toByteArray());
    }
}
