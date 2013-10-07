package org.motechproject.email.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.email.domain.DeliveryStatus;
import org.motechproject.email.domain.EmailRecord;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailAuditService;
import org.motechproject.email.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static org.motechproject.commons.date.util.DateUtil.now;

/**
 * The <code>EmailSenderServiceImpl</code> class provides API for sending e-mails
 */

@Service("emailSenderService")
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailAuditService auditService;

    private static final Logger LOG = Logger.getLogger(EmailSenderServiceImpl.class);

    @Override
    public void send(final Mail mail) {
        LOG.info(String.format("Sending message [%s] from [%s] to [%s] with subject [%s].",
                mail.getMessage(), mail.getFromAddress(), mail.getToAddress(), mail.getSubject()));
        try {
            mailSender.send(getMimeMessagePreparator(mail));
            auditService.log(new EmailRecord(
                    mail.getFromAddress(), mail.getToAddress(), mail.getSubject(), mail.getMessage(),
                    now(), DeliveryStatus.SENT));
        } catch (MailException e) {
            auditService.log(new EmailRecord(
                    mail.getFromAddress(), mail.getToAddress(), mail.getSubject(), mail.getMessage(),
                    now(), DeliveryStatus.ERROR));
            throw e;
        }
    }

    MotechMimeMessagePreparator getMimeMessagePreparator(Mail mail) {
        return new MotechMimeMessagePreparator(mail);
    }
}
