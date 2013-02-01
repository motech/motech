package org.motechproject.sms.api.web;

import org.motechproject.sms.api.exceptions.SendSmsException;
import org.motechproject.sms.api.service.SendSmsRequest;
import org.motechproject.sms.api.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.EOFException;

@Controller
@RequestMapping("web-api")
public class SmsWebServiceController {

    @Autowired
    @Qualifier("smsServiceImpl")
    private SmsService smsService;

    public SmsWebServiceController() {
    }

    public SmsWebServiceController(SmsService smsService) {
        this();
        this.smsService = smsService;
    }

    @RequestMapping(value = "messages", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @PreAuthorize("hasRole('smsSend')")
    public void sendSMS(@RequestBody SendSmsRequest request) {
        smsService.sendSMS(request);
    }

    @ExceptionHandler({ EOFException.class, HttpMessageNotReadableException.class, SendSmsException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String badRequestHandler(Exception exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public String mediaTypeException(Exception exception) {
        return exception.getMessage();
    }
}
