package org.motechproject.commcare.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.commcare.domain.CommcareAccountSettings;
import org.motechproject.commcare.domain.CommcarePermission;
import org.motechproject.commcare.exception.CommcareAuthenticationException;
import org.motechproject.commcare.exception.CommcareConnectionFailureException;
import org.motechproject.commcare.service.CommcareAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

@Controller
@RequestMapping("/connection")
public class StubConnectionController {

    private CommcareAccountService commcareAccountService;

    @Autowired
    public StubConnectionController(CommcareAccountService commcareAccountService) {
        this.commcareAccountService = commcareAccountService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public void verifySettings(@RequestBody CommcareAccountSettings settings) throws CommcareAuthenticationException, CommcareConnectionFailureException {
        commcareAccountService.verifySettings(settings);
    }

    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    @ResponseBody
    public List<CommcarePermission> getPermissions() throws CommcareAuthenticationException, CommcareConnectionFailureException {
        return asList(new CommcarePermission("modify CommCareHQ settings", true), new CommcarePermission("make API calls", false));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleIllegalArgumentException(IllegalArgumentException exception) throws IOException {
        return new ObjectMapper().writeValueAsString(new ErrorText(exception.getMessage()));
    }

    @ExceptionHandler(CommcareConnectionFailureException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleCommcareConnectionFailureException(CommcareConnectionFailureException exception) throws IOException {
        return new ObjectMapper().writeValueAsString(new ErrorText(exception.getMessage()));
    }

    @ExceptionHandler(CommcareAuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleCommcareAuthenticationException(CommcareAuthenticationException exception) throws IOException {
        return new ObjectMapper().writeValueAsString(new ErrorText(exception.getMessage()));
    }
}

class ErrorText {

    private String message;

    ErrorText(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
