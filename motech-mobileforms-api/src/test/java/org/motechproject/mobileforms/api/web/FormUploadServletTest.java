package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZInputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.fcitmuk.epihandy.ResponseHeader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mobileforms.api.callbacks.StudyProcessor;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.service.UsersService;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
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
    private StudyProcessor studyProcessor;
    private Integer groupIndex = 2;

    @Before
    public void setup() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        formUploadServlet = spy(new FormUploadServlet());
        ReflectionTestUtils.setField(formUploadServlet, "context", applicationContext);
        ReflectionTestUtils.setField(formUploadServlet, "mobileFormsService", mobileFormsService);
        ReflectionTestUtils.setField(formUploadServlet, "usersService", usersService);
        ReflectionTestUtils.setField(formUploadServlet, "studyProcessor", studyProcessor);
        doReturn(epihandySerializer).when(formUploadServlet).serializer();
    }

    @Test
    public void shouldProcessUploadedForms() throws Exception {
        int processedForms = 6;
        int failedForms = 0;
        Map<Integer, String> formIdMap = new HashMap<Integer, String>();
        when(studyProcessor.formsCount()).thenReturn(processedForms);
        when(mobileFormsService.getFormIdMap()).thenReturn(formIdMap);

        populateHttpRequest(request, "username", "password", groupIndex);

        formUploadServlet.doPost(request, response);
        String responseSentToMobile = readResponse(response);
        byte[] expected = new byte[9];
        expected[0] = ResponseHeader.STATUS_SUCCESS;
        expected[4] = (byte) processedForms;
        expected[8] = (byte) failedForms;

        assertEquals(new String(expected), new String(responseSentToMobile.getBytes("UTF8")));

        verify(epihandySerializer).addDeserializationListener(studyProcessor);
        verify(epihandySerializer).deserializeStudiesWithEvents(any(DataInputStream.class), eq(formIdMap));

    }

    private String readResponse(MockHttpServletResponse response) throws IOException {
        return new BufferedReader(new InputStreamReader(new ZInputStream(new ByteArrayInputStream(response.getContentAsByteArray())))).readLine();
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
}
