package org.motechproject.sms.api.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.sms.api.exceptions.SendSmsException;
import org.motechproject.sms.api.service.SendSmsRequest;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationSmsApi.xml"})
public class SmsWebServiceIT {

    @Mock
    private SmsService smsService;

    SmsWebServiceController smsWebService;

    MockMvc controllerWithMockService;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        smsWebService = new SmsWebServiceController(smsService);
        controllerWithMockService = MockMvcBuilders.standaloneSetup(smsWebService).build();
    }

    @Test
    public void shouldReturn400ForInvalidRequest() throws Exception {
        doThrow(new SendSmsException(new IllegalArgumentException("bad"))).when(smsService).sendSMS(any(SendSmsRequest.class));
        controllerWithMockService.perform(
            post("/web-api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(new SendSmsRequest(null, "hello")).getBytes("UTF-8")))
            .andExpect(
                status().is(400))
            .andExpect(
                content().string("java.lang.IllegalArgumentException: bad")
            );
    }

    @Test
    public void shouldReturn400ForInvalidJson() throws Exception {
        controllerWithMockService.perform(
            post("/web-api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body("goobar".getBytes("UTF-8")))
            .andExpect(
                status().is(400));
    }

    @Test
    public void shouldSendSmsUsingSmsService() throws Exception {
        controllerWithMockService.perform(
            post("/web-api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(new SendSmsRequest(asList("123"), "hello")).getBytes("UTF-8")))
        .andExpect(
            status().is(201));
        verify(smsService).sendSMS(new SendSmsRequest(asList("123"), "hello"));
    }
}
