package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZInputStream;
import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.valueobjects.GroupNameAndForms;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormDownloadServletTest {

    private FormDownloadServlet formDownloadServlet;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MobileFormsService mobileFormsService;

    @Mock
    private EpihandyXformSerializer epihandyXformSerializer;

    @Before
    public void setUp() {
        initMocks(this);
        FormDownloadServlet formDownloadServlet = new FormDownloadServlet();
        this.formDownloadServlet = spy(formDownloadServlet);
        ReflectionTestUtils.setField(this.formDownloadServlet, "context", applicationContext);
        doReturn(epihandyXformSerializer).when(this.formDownloadServlet).serializer();

        when(applicationContext.getBean("mobileFormsService", MobileFormsService.class)).thenReturn(mobileFormsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldReturnListOfStudyNames() {
        String studyCategoryOne = "StudyOne";
        String studyCategoryTwo = "StudyTwo";
        final String dataExpectedToBeReturned = "Mock - List of form groups";

        when(mobileFormsService.getAllFormGroups()).thenReturn(
                Arrays.asList(new Object[]{0, studyCategoryOne}, new Object[]{1, studyCategoryTwo}));
        try {
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    ((ByteArrayOutputStream) invocationOnMock.getArguments()[0]).write(dataExpectedToBeReturned.getBytes());
                    return null;
                }
            }).when(epihandyXformSerializer).serializeStudies(any(OutputStream.class), Matchers.anyObject());

            setupRequestWithActionOtherRequestParamenters(request, "username", "password", FormDownloadServlet.ACTION_DOWNLOAD_STUDY_LIST, null);

            formDownloadServlet.doPost(request, response);
            String responseSentToMobile = readResponse(response);

            ArgumentCaptor<OutputStream> outputStreamArgumentCaptor = ArgumentCaptor.forClass(OutputStream.class);
            ArgumentCaptor<Object> dataArgumentCaptor = ArgumentCaptor.forClass(Object.class);
            verify(epihandyXformSerializer).serializeStudies(outputStreamArgumentCaptor.capture(), dataArgumentCaptor.capture());
            assertThat(outputStreamArgumentCaptor.getValue().toString(), is(equalTo(dataExpectedToBeReturned)));

            assertThat(((List<Object[]>)dataArgumentCaptor.getValue()).size(), is(equalTo(2)));
            assertTrue(Arrays.equals(((List<Object[]>)dataArgumentCaptor.getValue()).get(0), new Object[]{0, studyCategoryOne}));
            assertTrue(Arrays.equals(((List<Object[]>)dataArgumentCaptor.getValue()).get(1), new Object[]{1, studyCategoryTwo}));

            assertThat(responseSentToMobile, is(equalTo((char)FormDownloadServlet.RESPONSE_SUCCESS + dataExpectedToBeReturned)));

        } catch (Exception e) {
            assertFalse("Test failed, cause: " + e.getMessage(), true);
        }
    }

    @Test
    public void shouldReturnListOfFormsForTheGivenStudyName(){
        String formOneContent = "FormOne";
        String formTwoConent = "FromTwo";
        String groupName = "GroupOne";
        Integer groupIndex = 2;
        final String dataExpectedToBeReturned = "Mock - List of forms";
        when(mobileFormsService.getForms(groupIndex)).thenReturn(new GroupNameAndForms(groupName, Arrays.asList(formOneContent, formTwoConent)));

        try {
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                    ((ByteArrayOutputStream) invocationOnMock.getArguments()[0]).write(dataExpectedToBeReturned.getBytes());
                    return null;
                }
            }).when(epihandyXformSerializer).serializeForms(any(OutputStream.class), Matchers.anyObject(), anyInt(), anyString());

            setupRequestWithActionOtherRequestParamenters(request, "username", "password", FormDownloadServlet.ACTION_DOWNLOAD_USERS_AND_FORMS, groupIndex);

            formDownloadServlet.doPost(request, response);
            String responseSentToMobile = readResponse(response);

            verifyIfSerializerWasCalledWithRightArguments(dataExpectedToBeReturned, Arrays.asList(formOneContent, formTwoConent), groupIndex, groupName);
            assertThat(responseSentToMobile, is(equalTo((char)FormDownloadServlet.RESPONSE_SUCCESS + dataExpectedToBeReturned)));

        } catch (Exception e) {

            assertFalse("Test failed, cause: " + e.getMessage(), true);
        }

    }

    private void verifyIfSerializerWasCalledWithRightArguments(String dataExpectedToBeReturned, List<String> contentsOfForms, Integer groupIndex, String groupName) throws Exception {
        ArgumentCaptor<OutputStream> outputStreamArgumentCaptor = ArgumentCaptor.forClass(OutputStream.class);
        ArgumentCaptor<Object> dataArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<Integer> studyIdArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> studyNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(epihandyXformSerializer).serializeForms(outputStreamArgumentCaptor.capture(), dataArgumentCaptor.capture(), studyIdArgumentCaptor.capture(), studyNameArgumentCaptor.capture());
        assertThat(outputStreamArgumentCaptor.getValue().toString(), is(equalTo(dataExpectedToBeReturned)));
        assertThat((List<String>) dataArgumentCaptor.getValue(), is(equalTo(contentsOfForms)));
        assertThat(studyIdArgumentCaptor.getValue(), is(equalTo(groupIndex)));
        assertThat(studyNameArgumentCaptor.getValue(), is(equalTo(groupName)));
    }


    private String readResponse(MockHttpServletResponse response) throws IOException {
        return new BufferedReader(new InputStreamReader(new ZInputStream(new ByteArrayInputStream(response.getContentAsByteArray())))).readLine();
    }

    private void setupRequestWithActionOtherRequestParamenters(MockHttpServletRequest request, String userName, String password, byte actionCode, Integer groupIndex) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF(userName);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF("epihandyser");
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeByte(actionCode);

        if (groupIndex != null) {
            dataOutputStream.writeInt(groupIndex);
        }
        request.setContent(byteArrayOutputStream.toByteArray());
    }
}
