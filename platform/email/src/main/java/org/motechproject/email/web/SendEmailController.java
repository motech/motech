package org.motechproject.email.web;

import org.motechproject.email.constants.EmailRolesConstants;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
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

import static org.joda.time.DateTime.now;

/**
 * The <code>SendEmailController</code> class is responsible for handling requests connected with sending e-mails
 */

@Controller
public class SendEmailController {
    private EmailSenderService senderService;
    private EmailAuditService auditService;
    private Mail mailAttempt;

    public SendEmailController() {
        this(null, null);
    }

    @Autowired
    public SendEmailController(EmailSenderService senderService, EmailAuditService auditService) {
        this.senderService = senderService;
        this.auditService = auditService;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    @PreAuthorize(EmailRolesConstants.HAS_ANY_EMAIL_ROLE)
    @ResponseStatus(HttpStatus.OK)
    public void sendEmail(@RequestBody Mail mail) {
        mailAttempt = mail;
        senderService.send(mail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        auditService.log(new EmailRecord(mailAttempt.getFromAddress(), mailAttempt.getToAddress(),
                mailAttempt.getSubject(), mailAttempt.getMessage(), now(), DeliveryStatus.ERROR));
        return e.getMessage();
    }
}