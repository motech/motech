package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.service.ChannelService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChannelControllerTest {

    @Mock
    ChannelService channelService;

    @Mock
    HttpServletResponse response;

    @Mock
    ServletOutputStream outputStream;

    ChannelController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ChannelController(channelService);
    }

    @Test
    public void shouldGetAllChannels() {
        List<Channel> expected = new ArrayList<>();
        expected.add(new Channel());
        expected.add(new Channel());
        expected.add(new Channel());

        when(channelService.getAllChannels()).thenReturn(expected);

        List<Channel> actual = controller.getAllChannels();

        verify(channelService).getAllChannels();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetChannelIcon() throws IOException {
        String moduleName = "test";
        String moduleVersion = "0.15";

        verifyGetChannelIcon(moduleName, moduleVersion, times(1));
    }

    @Test
    public void shouldNotGetChannelIconWhenNameOrVersionIsEmpty() throws IOException {
        String moduleName = "test";
        String moduleVersion = "0.15";
        VerificationMode mode = never();

        verifyGetChannelIcon(moduleName, "", mode);
        verifyGetChannelIcon("", moduleVersion, mode);
        verifyGetChannelIcon("", "", mode);

        verifyGetChannelIcon(moduleName, null, mode);
        verifyGetChannelIcon(null, moduleVersion, mode);
        verifyGetChannelIcon(null, null, mode);
    }

    private void verifyGetChannelIcon(String moduleName, String moduleVersion, VerificationMode mode) throws IOException {
        BundleIcon icon = new BundleIcon(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, "image/jpeg");

        when(channelService.getChannelIcon(moduleName, moduleVersion)).thenReturn(icon);
        when(response.getOutputStream()).thenReturn(outputStream);

        controller.getChannelIcon(moduleName, moduleVersion, response);

        verify(response, mode).setStatus(HttpServletResponse.SC_OK);
        verify(response, mode).setContentLength(icon.getContentLength());
        verify(response, mode).setContentType(icon.getMime());
        verify(response, mode).getOutputStream();

        verify(outputStream, mode).write(icon.getIcon());
    }

}
