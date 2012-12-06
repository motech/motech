package org.motechproject.sms.http.web;

import org.motechproject.sms.api.SmsDeliveryFailureException;
import org.motechproject.sms.http.service.SmsHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


/*
relative to smshttp/api
*/

@Controller
public class SMSController {

    private static Logger log = LoggerFactory.getLogger(SMSController.class);

    private SmsHttpService smsHttpService;

    @Autowired
    public SMSController(SmsHttpService smsHttpService) {
        this.smsHttpService = smsHttpService;
    }

    @RequestMapping(value = "/outbound", headers = "Content-Type=application/json")
    public ResponseEntity<String> send(@RequestBody final SMSRequest smsRequest) throws SmsDeliveryFailureException {
        log.info(String.format("Sending message : %s to recipients %s", smsRequest.getMessage(), smsRequest.getRecipient()));
        if (smsRequest.isValid()) {
            smsHttpService.sendSMS(smsRequest.getRecipient(), smsRequest.getMessage());
            log.info(String.format("Sent message : %s to recipients %s", smsRequest.getMessage(), smsRequest.getRecipient()));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        log.info(String.format("Could not send message : %s to recipients %s : Invalid data", smsRequest.getMessage(), smsRequest.getRecipient()));
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
