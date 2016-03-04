package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TriggerEventService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChannelControllerTest {

    @Mock
    ChannelService channelService;

    @Mock
    TriggerEventService triggerEventService;

    ChannelController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ChannelController(channelService, triggerEventService);
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
}
