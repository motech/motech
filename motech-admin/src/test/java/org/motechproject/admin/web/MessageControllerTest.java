package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.web.controller.MessageController;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageControllerTest {

    @InjectMocks
    MessageController controller = new MessageController();

    @Mock
    StatusMessageService statusMessageService;

    @Mock
    List<StatusMessage> statusMessages;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetAllMessages() {
        when(statusMessageService.getAllMessages()).thenReturn(statusMessages);

        List<StatusMessage> result = controller.getMessages(true);

        assertEquals(statusMessages, result);
        verify(statusMessageService).getAllMessages();
    }

    @Test
    public void testGetActiveMessagesNo() {
        when(statusMessageService.getActiveMessages()).thenReturn(statusMessages);

        List<StatusMessage> result = controller.getMessages(false);

        assertEquals(statusMessages, result);
        verify(statusMessageService).getActiveMessages();
    }
}
