package org.motechproject.sms.http;

import org.junit.Test;
import org.motechproject.sms.http.template.SmsHttpTemplate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SMSGatewayResponseTest {

    private final String SUCCESS_RESPONSE_PATTERN
            = "\\w*<\\?xml version=\"1.0\" encoding=\"utf-8\"\\?>\\r\\n<string xmlns=\"http://yellowpepper.com/webservices/literalTypes\">+(?!9989|9969|9959|9979|9949|9939|8998|9919|9999|9900|9909).\\w+</string>";

    @Test
    public void shouldConsiderBlankResponseAsFailure() {
        assertThat(new SMSGatewayResponse(new SmsHttpTemplate(), "").isSuccess(), is(false));
        assertThat(new SMSGatewayResponse(new SmsHttpTemplate(), null).isSuccess(), is(false));
    }

    @Test
    public void shouldConsiderResponseThatDoesNotMatchTemplateResponsePatternAsFailure() {
        SmsHttpTemplate smsHttpTemplate = mock(SmsHttpTemplate.class);
        when(smsHttpTemplate.getSuccessfulResponsePattern()).thenReturn(SUCCESS_RESPONSE_PATTERN);
        SMSGatewayResponse gatewayResponse = new SMSGatewayResponse(smsHttpTemplate,
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://yellowpepper.com/webservices/literalTypes\">990964a0f3a016084c52</string>");
        assertThat(gatewayResponse.isSuccess(), is(false));
    }

    @Test
    public void shouldConsiderResponseMatchingTemplateResponsePatternAsSuccess() {
        SmsHttpTemplate smsHttpTemplate = mock(SmsHttpTemplate.class);
        when(smsHttpTemplate.getSuccessfulResponsePattern()).thenReturn(SUCCESS_RESPONSE_PATTERN);
        SMSGatewayResponse gatewayResponse
                = new SMSGatewayResponse(smsHttpTemplate, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n<string xmlns=\"http://yellowpepper.com/webservices/literalTypes\">998864a0f3a016084c52</string>");
        assertThat(gatewayResponse.isSuccess(), is(true));
    }


}
