package org.motechproject.security.web.controllers;

import org.motechproject.security.model.UserDto;
import org.motechproject.security.service.MotechUserProfile;
import org.motechproject.security.service.MotechUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/users/create", method = RequestMethod.POST)
    public void saveUser(@RequestBody UserDto user) {
        motechUserService.register(user.getUserName(), user.getPassword(), user.getEmail(), "", user.getRoles());
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<MotechUserProfile> getUsers() {
        return motechUserService.getUsers();
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


}
