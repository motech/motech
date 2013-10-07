package org.motechproject.email.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.email.constants.SendEmailConstants.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SendEmailControllerTest {

    @Mock
    private EmailSenderService senderService;

    private SendEmailController sendEmailController;

    private MockMvc controller;

    private Mail mail = new Mail("from", "to", "subject", "message");

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        sendEmailController = new SendEmailController(senderService);
        controller = MockMvcBuilders.standaloneSetup(sendEmailController).build();
    }

    @Test
    public void shouldSendEmail() throws Exception {
        sendEmailController.sendEmail(mail);

        verify(senderService).send(mail);
    }

    @Test
    public void shouldExecuteSendEmailRequest() throws Exception {
        controller.perform(
                post("/send").body(convertMailToJson()).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        );
    }

    @Test
    public void shouldHandleExceptionDuringExecutionSendEmailRequest() throws Exception {
        String message = "There are problems with sending email";
        doThrow(new IllegalStateException(message)).when(senderService).send(mail);

        controller.perform(
                post("/send").body(convertMailToJson()).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(message)
        );
    }

    private byte[] convertMailToJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();

        json.put(FROM_ADDRESS, mail.getFromAddress());
        json.put(TO_ADDRESS, mail.getToAddress());
        json.put(SUBJECT, mail.getSubject());
        json.put(MESSAGE, mail.getMessage());

        return json.toString().getBytes();
    }
}