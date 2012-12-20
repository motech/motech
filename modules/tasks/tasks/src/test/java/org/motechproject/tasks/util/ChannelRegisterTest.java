package org.motechproject.tasks.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.service.ChannelService;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChannelRegisterTest {

    @Mock
    InputStream inputStream;

    @Mock
    Resource resource;

    @Mock
    ChannelService channelService;

    ChannelRegister channelRegister;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        channelRegister = new ChannelRegister(resource);
    }

    @Test
    public void shouldRegisterChannelWhenChannelServiceIsAvailable() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        channelRegister.registered(channelService, null);

        verify(channelService).registerChannel(inputStream);
    }

    @Test
    public void shouldNotRegisterChannelWhenGotOtherServices() throws IOException {
        when(resource.getInputStream()).thenReturn(inputStream);

        channelRegister.registered(new Object(), null);

        verify(channelService, never()).registerChannel(inputStream);
    }
}
