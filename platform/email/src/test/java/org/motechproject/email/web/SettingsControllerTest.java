package org.motechproject.email.web;

import org.apache.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.email.purging.EmailPurger;
import org.motechproject.email.settings.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map.Entry;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.email.settings.SettingsDto.*;
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
    private static final String USERNAME = "user";
    private static final String PASSWORD = "8099";
    private static final String LOG_ADDRESS = "true";
    private static final String LOG_SUBJECT = "true";
    private static final String LOG_BODY = "true";
    private static final String LOG_PURGE = "true";
    private static final String LOG_TIME = "1";
    private static final String LOG_MULTIPLIER = "weeks";
    private static final InputStream ADDITIONAL_PROPERTIES = new StringBufferInputStream("mail.smtp.starttls.enable=true\nmail.smtp.auth=true");

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private JavaMailSenderImpl javaMailSender;

    @Mock
    private EmailPurger emailPurger;

    private MockMvc controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(settingsFacade.getProperty(MAIL_HOST_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(HOST);
        when(settingsFacade.getProperty(MAIL_PORT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(PORT);
        when(settingsFacade.getProperty(MAIL_USERNAME_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(USERNAME);
        when(settingsFacade.getProperty(MAIL_PASSWORD_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(PASSWORD);
        when(settingsFacade.getRawConfig(EMAIL_ADDITIONAL_PROPERTIES_FILE_NAME)).thenReturn(ADDITIONAL_PROPERTIES);
        when(settingsFacade.getProperty(MAIL_LOG_ADDRESS_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_ADDRESS);
        when(settingsFacade.getProperty(MAIL_LOG_SUBJECT_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_SUBJECT);
        when(settingsFacade.getProperty(MAIL_LOG_BODY_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_BODY);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_ENABLE_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_PURGE);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_PROPERY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_TIME);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_MULTIPLIER_PROPERTY, EMAIL_PROPERTIES_FILE_NAME)).thenReturn(LOG_MULTIPLIER);

        controller = MockMvcBuilders.standaloneSetup(new SettingsController(settingsFacade, javaMailSender, emailPurger)).build();
    }

    @Test
    public void shouldReturnSettingsDto() throws Exception {
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        controller.perform(
                get("/settings")
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string(jsonMatcher(
                        settingsJson(HOST, PORT, USERNAME, PASSWORD, properties, LOG_ADDRESS, LOG_SUBJECT, LOG_BODY, LOG_PURGE, LOG_TIME, LOG_MULTIPLIER)
                ))
        );
    }

    @Test
    public void shouldChangeSettings() throws Exception {
        String remotehost = "remotehost";
        String port = "9999";
        String username = "user";
        String password = "pass";
        Properties additionalProperties = new Properties();
        String logAddress = "false";
        String logSubject = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/settings").body(
                        settingsJson(
                                remotehost, port, username, password, additionalProperties, logAddress, logSubject, logBody, logPurge, logPurgeTime,
                                logPurgeMultiplier
                        ).getBytes()
                ).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        );

        Properties properties = new SettingsDto(
                remotehost, port, username, password, additionalProperties, logAddress, logSubject, logBody, logPurge, logPurgeTime,logPurgeMultiplier
        ).toProperties();

        verify(settingsFacade).saveConfigProperties(EMAIL_PROPERTIES_FILE_NAME, properties);
        verify(javaMailSender).setHost(remotehost);
        verify(javaMailSender).setPort(Integer.valueOf(port));
    }

    @Test
    public void shouldNotChangeSettingsWhenHostIsBlank() throws Exception {
        String port = "9999";
        String username = "user";
        String password = "pass";
        Properties additionalProperties = new Properties();
        String logAddress = "false";
        String logSubject = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/settings").body(settingsJson(
                        "", port, username, password, additionalProperties, logAddress, logSubject, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
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
        String username = "user";
        String password = "pass";
        Properties additionalProps = new Properties();
        String logAddress = "false";
        String logSubject = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/settings").body(settingsJson(
                        remotehost, "", username, password, additionalProps, logAddress, logSubject, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
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
        String username = "user";
        String password = "pass";
        Properties additionalProps = new Properties();
        String logAddress = "false";
        String logSubject = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/settings").body(settingsJson(
                        remotehost, port, username, password, additionalProps, logAddress, logSubject, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(NUMERIC_FORMAT, MAIL_PORT_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
        verify(javaMailSender, never()).setHost(anyString());
        verify(javaMailSender, never()).setPort(anyInt());
    }

    private String settingsJson(String host, String port, String username, String password, Properties props, String logAddress, String logSubject, String logBody,
                                String logPurgeEnable, String logPurgeTime, String logPurgeTimeMultiplier) {

        ObjectNode jsonNode = new ObjectMapper().createObjectNode();
        jsonNode.put("host", host);
        jsonNode.put("port", port);
        jsonNode.put("username", username);
        jsonNode.put("password", password);
        jsonNode.put("logAddress", logAddress);
        jsonNode.put("logSubject", logSubject);
        jsonNode.put("logBody", logBody);
        jsonNode.put("logPurgeEnable", logPurgeEnable);
        jsonNode.put("logPurgeTime", logPurgeTime);
        jsonNode.put("logPurgeTimeMultiplier", logPurgeTimeMultiplier);
        ObjectNode jsonProps = new ObjectMapper().createObjectNode();
        if (props != null) {
            for (Entry<Object, Object> entry : props.entrySet()) {
                jsonProps.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
        jsonNode.put("additionalProperties",jsonProps);
        return jsonNode.toString();
    }
}
