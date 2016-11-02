package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.dto.ChannelDto;
import org.motechproject.tasks.service.TaskWebService;
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
    TaskWebService taskWebService;

    @Mock
    TriggerEventService triggerEventService;

    ChannelController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new ChannelController(taskWebService, triggerEventService);
    }

    @Test
    public void shouldGetAllChannels() {
        List<ChannelDto> expected = new ArrayList<>();
        expected.add(new ChannelDto());
        expected.add(new ChannelDto());
        expected.add(new ChannelDto());

        when(taskWebService.getAllChannels()).thenReturn(expected);

        List<ChannelDto> actual = controller.getAllChannels();

        verify(taskWebService).getAllChannels();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
