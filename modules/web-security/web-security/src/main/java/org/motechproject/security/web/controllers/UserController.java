package org.motechproject.security.web.controllers;

import org.apache.commons.lang.StringUtils;
import org.motechproject.security.helper.AuthenticationMode;
import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private MotechUserService motechUserService;

    @Autowired
    private PlatformSettingsService settingsService;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        motechUserService.register(user.getUserName(), user.getPassword(), user.getEmail(), "", user.getRoles());
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        List<MotechUserProfile> users;
        String loginMode = settingsService.getPlatformSettings().getLoginMode();

        if (StringUtils.equalsIgnoreCase(loginMode, AuthenticationMode.OPEN_ID)) {
            users = motechUserService.getOpenIdUsers();
        } else {
            users = motechUserService.getUsers();
        }

        return users;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/getuser", method = RequestMethod.POST)
    @ResponseBody public UserDto getUser(@RequestBody String userName) {
        return motechUserService.getUser(userName);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/update", method = RequestMethod.POST)
    public void updateUser(@RequestBody UserDto user) {
        motechUserService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/delete", method = RequestMethod.POST)
    public void deleteUser(@RequestBody UserDto user) {
        motechUserService.deleteUser(user);
    }

    @RequestMapping(value = "/users/loginmode", method = RequestMethod.GET)
    @ResponseBody
    public String loginMode() {
        MotechSettings settings = settingsService.getPlatformSettings();
        return settings.getLoginMode().toLowerCase();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/{userName}/change/email", method = RequestMethod.POST)
    public void changeEmail(@PathVariable String userName, @RequestBody String email) {
        UserDto dto = motechUserService.getUser(userName);
        dto.setEmail(email);

        motechUserService.updateUser(dto);
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

}
