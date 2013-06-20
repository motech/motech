package org.motechproject.email.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.email.model.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.email.model.SettingsDto.EMAIL_PROPERTIES_FILE_NAME;
import static org.motechproject.email.model.SettingsDto.MAIL_HOST_PROPERTY;
import static org.motechproject.email.model.SettingsDto.MAIL_PORT_PROPERTY;
import static org.motechproject.testing.utils.rest.RestTestUtil.jsonMatcher;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class SettingsControllerTest {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String REQUIRED_FORMAT = "%s is required" + NEW_LINE;
    private static final String NUMERIC_FORMAT = "%s must be numeric" + NEW_LINE;

    private static final String HOST = "localhost";
    private static final String PORT = "8099";

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    private MockMvc controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(settingsFacade.getProperty(MAIL_HOST_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(HOST);
        when(settingsFacade.getProperty(MAIL_PORT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(PORT);

        controller = MockMvcBuilders.standaloneSetup(new SettingsController(settingsFacade, javaMailSender)).build();
    }

    @Test
    public void shouldReturnSettingsDto() throws Exception {
        controller.perform(
                get("/settings")
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string(jsonMatcher(settingsJson(HOST, PORT)))
        );
    }

    @Test
    public void shouldChangeSettings() throws Exception {
        String remotehost = "remotehost";
        String port = "9999";

        controller.perform(
                post("/settings").body(settingsJson(remotehost, port).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        );

        Properties properties = new SettingsDto(remotehost, port).toProperties();

        verify(settingsFacade).saveConfigProperties(EMAIL_PROPERTIES_FILE_NAME, properties);
        verify(javaMailSender).setHost(remotehost);
        verify(javaMailSender).setPort(Integer.valueOf(port));
    }

    @Test
    public void shouldNotChangeSettingsWhenHostIsBlank() throws Exception {
        String port = "9999";

        controller.perform(
                post("/settings").body(settingsJson("", port).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(REQUIRED_FORMAT, MAIL_HOST_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
        verify(javaMailSender, never()).setHost(anyString());
        verify(javaMailSender, never()).setPort(anyInt());
    }

    @Test
    public void shouldNotChangeSettingsWhenPortIsBlank() throws Exception {
        String remotehost = "remotehost";

        controller.perform(
                post("/settings").body(settingsJson(remotehost, "").getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(REQUIRED_FORMAT, MAIL_PORT_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
        verify(javaMailSender, never()).setHost(anyString());
        verify(javaMailSender, never()).setPort(anyInt());
    }

    @Test
    public void shouldNotChangeSettingsWhenPortIsNotNumeric() throws Exception {
        String remotehost = "remotehost";
        String port = "9999a";

        controller.perform(
                post("/settings").body(settingsJson(remotehost, port).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(NUMERIC_FORMAT, MAIL_PORT_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
        verify(javaMailSender, never()).setHost(anyString());
        verify(javaMailSender, never()).setPort(anyInt());
    }

    private String settingsJson(String host, String port) {
        ObjectNode jsonNode = new ObjectMapper().createObjectNode();
        jsonNode.put("host", host);
        jsonNode.put("port", port);

        return jsonNode.toString();
    }
}
