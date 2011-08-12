package org.motechproject.mobileforms.api.web;

import com.jcraft.jzlib.ZInputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.DelegatingServletOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormDownloadServletTest {

    @Mock
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        initMocks(request);
        initMocks(response);
    }

    @Test
    public void testDoPost() {
        FormDownloadServlet servlet = new FormDownloadServlet();
        String encodedOutput = null;

        try {
            final ByteArrayInputStream byteArrayInputStream = getInputStream();
            when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(byteArrayInputStream));
            when(response.getOutputStream()).thenReturn(getOutputstream());

            servlet.doPost(request, response);
            encodedOutput = getOutputString();
            verify(response).setStatus(HttpServletResponse.SC_OK);
        } catch (ServletException e) {
            assertFalse(true);
        } catch (IOException e) {
            assertFalse(true);
        }

        assertTrue(encodedOutput.contains("GroupName-I"));
        assertTrue(encodedOutput.contains("GroupName-II"));
    }

    private String getOutputString() throws IOException {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) ((DelegatingServletOutputStream) response.getOutputStream()).getTargetStream();
        ZInputStream zin = new ZInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        InputStreamReader reader = new InputStreamReader(zin);
        return new BufferedReader(reader).readLine();
    }

    private DelegatingServletOutputStream getOutputstream() throws ServletException, IOException {
        ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
        return new DelegatingServletOutputStream(targetStream);
    }

    private ByteArrayInputStream getInputStream() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        DataOutputStream dos = new DataOutputStream(bo);
        dos.writeUTF("motech");
        dos.writeUTF("ghs");
        dos.writeUTF("epihandyser");
        dos.writeUTF("en");
        dos.writeByte(2);
        byte buf[] = bo.toByteArray();

        return new ByteArrayInputStream(buf);
    }
}
