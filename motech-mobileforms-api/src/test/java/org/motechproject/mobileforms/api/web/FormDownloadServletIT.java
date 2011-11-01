package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZInputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mobileforms.api.utils.TestUtilities.*;

public class FormDownloadServletIT {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp() {
        initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void shouldReturnTheListOfFormGroups() {
        FormDownloadServlet servlet = new FormDownloadServlet();
        List<Byte[]> responseSentToMobile = null;
        try {
            setupRequestWithActionAndOtherRequestParameters(request, "username", "password", FormDownloadServlet.ACTION_DOWNLOAD_STUDY_LIST, null);

            servlet.doPost(request, response);
            responseSentToMobile = readResponse(response);

            assertThat(response.getStatus(), is(equalTo(HttpServletResponse.SC_OK)));

        } catch (ServletException e) {
            assertFalse(true);
        } catch (IOException e) {
            assertFalse(true);
        }

        assertThat(responseSentToMobile.get(0)[0], is(equalTo(FormDownloadServlet.RESPONSE_SUCCESS)));
        String responseSentToMobileAsString = new String(toPrimitive(responseSentToMobile.get(1)));
        assertTrue(responseSentToMobileAsString.contains("GroupNameI"));
        assertTrue(responseSentToMobileAsString.contains("GroupNameII"));
    }

    private List<Byte[]> readResponse(MockHttpServletResponse response) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ZInputStream(new ByteArrayInputStream(response.getContentAsByteArray())));
        return slice(toObjectByteArray(readFully(dataInputStream)), 1);
    }


    private void setupRequestWithActionAndOtherRequestParameters(MockHttpServletRequest request, String userName, String password, byte actionCode, Integer groupIndex) throws IOException {
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
