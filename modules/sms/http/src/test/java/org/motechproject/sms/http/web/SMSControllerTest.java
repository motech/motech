package org.motechproject.sms.http.web;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.http.domain.SMSRequest;
import org.motechproject.sms.http.service.SmsHttpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SMSControllerTest {


    @Mock
    private SmsHttpService smsHttpService;
    private SMSController smsController;


    @Before
    public void setUp() throws Exception {
        smsController = new SMSController(smsHttpService);
    }

    @Test
    public void shouldSendSMS() throws SmsDeliveryFailureException {

        String message = "Hello World";
        String recipient = "9886991167";
        ResponseEntity<String> responseEntity = smsController.send(new SMSRequest(message, recipient));

        assertThat(HttpStatus.OK, Is.is(responseEntity.getStatusCode()));

        verify(smsHttpService).sendSMS(recipient, message);

    }


    @Test
    public void shouldReturnInvalidRequestIfPhoneNumberInvalid() throws SmsDeliveryFailureException {
        ResponseEntity<String> responseEntity = smsController.send(new SMSRequest("message", ""));
        assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.BAD_REQUEST));
    }


//    @Test
//    public void shouldReturnInternalServerErrorIfException() throws SmsDeliveryFailureException {
//        String exceptionMessage = "Unknown format";
//        doThrow(new SmsDeliveryFailureException(exceptionMessage)).when(smsHttpService).sendSMS(anyString(), anyString());
//
//        ResponseEntity<String> responseEntity = smsController.send(new SMSRequest("message", "12345"));
//
//        assertThat(responseEntity.getStatusCode(), Is.is(HttpStatus.INTERNAL_SERVER_ERROR));
//        assertThat(responseEntity.getBody(), Is.is(exceptionMessage));
//
//    }

}
