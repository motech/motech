package org.motechproject.security.web.controllers;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.motechproject.osgi.web.LocaleService;
import org.motechproject.security.config.SettingService;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.ex.EmailExistsException;
import org.motechproject.security.ex.PasswordValidatorException;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

/**
 * The <code>UserController</code> class is responsible for handling web requests, connected with users.
 */
@Controller
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final int PASSWORD_LENGTH = 10;

    private MotechUserService motechUserService;

    private SettingsFacade settingsFacade;

    private SettingService settingService;

    private LocaleService localeService;

    /**
     * Creates user
     *
     * @param user user to be created
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        String password = user.isGeneratePassword() ? RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH) : user.getPassword();
        motechUserService.register(user.getUserName(), password, user.getEmail(), "", user.getRoles(), user.getLocale());
        motechUserService.sendLoginInformation(user.getUserName(), password);
    }

    /**
     * Gets all users
     *
     * @return list of users
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        return settingsFacade.getPlatformSettings().getLoginMode().isOpenId() ?
                motechUserService.getOpenIdUsers() :
                motechUserService.getUsers();
    }

    /**
     * Returns user with given name
     *
     * @param userName name of user
     * @return user with given name
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/getuser", method = RequestMethod.POST)
    @ResponseBody
    public UserDto getUser(@RequestBody String userName) {
        return motechUserService.getUser(userName);
    }

    /**
     * Gets user that is currently in session
     *
     * @return current user
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/current", method = RequestMethod.GET)
    @ResponseBody
    public UserDto currentUser() {
        return motechUserService.getCurrentUser();
    }

    /**
     * Updates given user
     *
     * @param user user to be updated
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public void updateUser(@RequestBody UserDto user) {
        if (StringUtils.isBlank(user.getPassword())) {
            motechUserService.updateUserDetailsWithoutPassword(user);
        } else {
            motechUserService.updateUserDetailsWithPassword(user);
        }
    }

    /**
     * Deletes given user
     *
     * @param user user to be removed
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public void deleteUser(@RequestBody UserDto user) {
        motechUserService.deleteUser(user);
    }

    /**
     * Gets current login mode
     *
     * @return current login mode
     */
    @RequestMapping(value = "/users/loginmode", method = RequestMethod.GET)
    @ResponseBody
    public String loginMode() {
        MotechSettings settings = settingsFacade.getPlatformSettings();
        return settings.getLoginMode().getName().toLowerCase();
    }

    /**
     * Changes email for current user
     *
     * @param email new email that should be used
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/change/email", method = RequestMethod.POST)
    public void changeEmail(@RequestBody String email) {
        motechUserService.changeEmail(email);
    }

    /**
     * Changes password for current user
     *
     * @param password array of strings that contains two passwords - both of them should be identical
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/change/password", method = RequestMethod.POST)
    public void changePassword(@RequestBody String[] password) {
        if (password.length == 2) {
            if (motechUserService.changePassword(password[0], password[1]) == null) {
                throw new IllegalArgumentException("User password and given password are not equal");
            }
        }
    }

    @ExceptionHandler(EmailExistsException.class)
    public void handleEmailExistsException(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.write("key:security.emailTaken");
        }
    }

    @ExceptionHandler(MailSendException.class)
    public void handleMailSendException(HttpServletResponse response) throws IOException {
        try (Writer writer = response.getWriter()) {
            writer.write("key:security.sendEmailException");
        }
    }

    @ExceptionHandler(PasswordValidatorException.class)
    public void handlePasswordValidatorException(HttpServletRequest request, HttpServletResponse response,
                                                 PasswordValidatorException ex) throws IOException {
        LOGGER.debug("Password did not pass validation: {}", ex.getMessage());

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Locale locale = localeService.getUserLocale(request);
        String errorMsg = settingService.getPasswordValidator().getValidationError(locale);

        try (Writer writer = response.getWriter()) {
            writer.write("literal:");
            writer.write(errorMsg);
        }
    }

    @Autowired
    public void setMotechUserService(MotechUserService motechUserService) {
        this.motechUserService = motechUserService;
    }

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    @Autowired
    public void setLocaleService(LocaleService localeService) {
        this.localeService = localeService;
    }
}
