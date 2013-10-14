package org.motechproject.email.web;

import org.motechproject.email.constants.EmailRolesConstants;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;


/**
 * The <code>SendEmailController</code> class is responsible for handling requests connected with sending e-mails
 */

@Controller
public class SendEmailController {
    private EmailSenderService senderService;

    public SendEmailController() {
        this(null);
    }

    @Autowired
    public SendEmailController(EmailSenderService senderService) {
        this.senderService = senderService;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody Mail mail) {
        senderService.send(mail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}