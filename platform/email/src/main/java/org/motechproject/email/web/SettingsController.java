package org.motechproject.email.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.motechproject.commons.api.MotechException;
import org.motechproject.email.purging.EmailPurger;
import org.motechproject.email.constants.EmailRolesConstants;
import org.motechproject.email.settings.SettingsDto;
import org.motechproject.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNumeric;
import static org.motechproject.email.settings.SettingsDto.MAIL_HOST_PROPERTY;
import static org.motechproject.email.settings.SettingsDto.MAIL_PORT_PROPERTY;
import static org.motechproject.email.settings.SettingsDto.EMAIL_ADDITIONAL_PROPERTIES_FILE_NAME;
import static org.motechproject.email.settings.SettingsDto.EMAIL_PROPERTIES_FILE_NAME;
import static org.motechproject.email.settings.SettingsDto.MAIL_LOG_PURGE_TIME_PROPERY;

/**
 * The <code>SettingsController</code> class is responsible for handling web requests, connected with settings in
 * the Email module
 */


@Controller
@Api(value="SettingsController", description = "The SettingsController class is responsible for handling web requests, connected with settings in\n" +
        "the Email module")
public class SettingsController {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String REQUIRED_FORMAT = "%s is required";
    private static final String NUMERIC_FORMAT = "%s must be numeric";
    private static final String TRUE = "true";

    private SettingsFacade settingsFacade;
    private JavaMailSenderImpl mailSender;
    private EmailPurger emailPurger;

    @Autowired
    public SettingsController(@Qualifier("emailSettings") SettingsFacade settingsFacade,
                              @Qualifier("mailSender") JavaMailSenderImpl mailSender,
                              EmailPurger emailPurger) {
        this.settingsFacade = settingsFacade;
        this.mailSender = mailSender;
        this.emailPurger = emailPurger;
    }

    public SettingsController() {
        this(null, null, null);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @ApiOperation(value="Gets the email grid settings")
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseBody
    public SettingsDto getSettings() {
        return new SettingsDto(settingsFacade);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ApiOperation(value="Sets the grid settings with the given settings")
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseStatus(HttpStatus.OK)
    public void setSettings(@RequestBody SettingsDto settings) {
        String host = settings.getHost();
        String port = settings.getPort();
        String username = settings.getUsername();
        String password = settings.getPassword();
        String days = settings.getLogPurgeTime();
        String purgeEnabled = settings.getLogPurgeEnable();
        StringBuilder exceptionMessage = new StringBuilder();

        if (isBlank(host)) {
            exceptionMessage
                    .append(String.format(REQUIRED_FORMAT, MAIL_HOST_PROPERTY))
                    .append(NEW_LINE);
        }

        if (isBlank(port)) {
            exceptionMessage
                    .append(String.format(REQUIRED_FORMAT, MAIL_PORT_PROPERTY))
                    .append(NEW_LINE);
        } else if (!isNumeric(port)) {
            exceptionMessage
                    .append(String.format(NUMERIC_FORMAT, MAIL_PORT_PROPERTY))
                    .append(NEW_LINE);
        }

        if (TRUE.equals(purgeEnabled) && (!isNumeric(days))) {
            exceptionMessage
                    .append(String.format(NUMERIC_FORMAT, MAIL_LOG_PURGE_TIME_PROPERY))
                    .append(NEW_LINE);
        }

        if (exceptionMessage.length() > 0) {
            throw new IllegalStateException(exceptionMessage.toString());
        }
        OutputStream os = new ByteArrayOutputStream();
        try {
            settings.getAdditionalProps().store(os, "AdditionalEmailProperties");
        } catch (IOException e) {
            throw new MotechException("Error parsing additional email properties", e);
        }
        settingsFacade.saveConfigProperties(EMAIL_PROPERTIES_FILE_NAME, settings.toProperties());
        settingsFacade.saveRawConfig(EMAIL_ADDITIONAL_PROPERTIES_FILE_NAME, new String(os.toString()));

        if (emailPurger != null) {
            emailPurger.handleSettingsChange();
        }

        mailSender.setHost(host);
        mailSender.setPort(Integer.valueOf(port));
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(settings.getAdditionalProps());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
