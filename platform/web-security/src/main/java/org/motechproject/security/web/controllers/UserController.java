package org.motechproject.security.web.controllers;

import org.apache.commons.lang.RandomStringUtils;
import org.motechproject.security.domain.MotechUserProfile;
import org.motechproject.security.ex.EmailExistsException;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.domain.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * The <code>UserController</code> class is responsible for handling web requests, connected with users.
 */
@Controller
public class UserController {

    private static final int PASSWORD_LENGTH = 10;

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private SettingsFacade settingsFacade;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        String password = user.isGeneratePassword() ? RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH) : user.getPassword();
        motechUserService.register(user.getUserName(), password, user.getEmail(), "", user.getRoles(), user.getLocale());
        motechUserService.sendLoginInformation(user.getUserName(), password);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        return settingsFacade.getPlatformSettings().getLoginMode().isOpenId() ?
                motechUserService.getOpenIdUsers() :
                motechUserService.getUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/getuser", method = RequestMethod.POST)
    @ResponseBody
    public UserDto getUser(@RequestBody String userName) {
        return motechUserService.getUser(userName);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/current", method = RequestMethod.GET)
    @ResponseBody
    public UserDto currentUser() {
        return motechUserService.getCurrentUser();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public void updateUser(@RequestBody UserDto user) {
        motechUserService.updateUserDetailsWithPassword(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public void deleteUser(@RequestBody UserDto user) {
        motechUserService.deleteUser(user);
    }

    @RequestMapping(value = "/users/loginmode", method = RequestMethod.GET)
    @ResponseBody
    public String loginMode() {
        MotechSettings settings = settingsFacade.getPlatformSettings();
        return settings.getLoginMode().getName().toLowerCase();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{userName}/change/email", method = RequestMethod.POST)
    public void changeEmail(@PathVariable String userName, @RequestBody String email) {
        UserDto dto = motechUserService.getUser(userName);
        dto.setEmail(email);

        motechUserService.updateUserDetailsWithoutPassword(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{userName}/change/password", method = RequestMethod.POST)
    public void changePassword(@PathVariable String userName, @RequestBody String[] password) {
        if (password.length == 2) {
            if (motechUserService.changePassword(userName, password[0], password[1]) == null) {
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
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        try (Writer writer = response.getWriter()) {
            writer.write("key:security.sendEmailException");
        }
    }

}
