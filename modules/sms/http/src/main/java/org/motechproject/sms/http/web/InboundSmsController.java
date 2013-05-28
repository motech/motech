package org.motechproject.sms.http.web;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.domain.SmsRecord;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Random;

import static org.motechproject.sms.api.DeliveryStatus.RECEIVED;
import static org.motechproject.sms.api.SMSType.INBOUND;
import static org.motechproject.sms.api.constants.EventDataKeys.INBOUND_MESSAGE;
import static org.motechproject.sms.api.constants.EventDataKeys.SENDER;
import static org.motechproject.sms.api.constants.EventDataKeys.TIMESTAMP;

@Controller
@RequestMapping("/sms")
public class InboundSmsController {

    private EventRelay eventRelay;
    private SmsHttpTemplate template;
    private SmsAuditService smsAuditService;

    @Autowired
    public InboundSmsController(TemplateReader templateReader, EventRelay eventRelay, SmsAuditService smsAuditService) {
        this.template = templateReader.getTemplate();
        this.eventRelay = eventRelay;
        this.smsAuditService = smsAuditService;
    }

    @RequestMapping(value = "inbound")
    public void handle(HttpServletRequest request) {
        String sender = request.getParameter(template.getIncoming().getSenderKey());
        String message = request.getParameter(template.getIncoming().getMessageKey());
        String timestamp = request.getParameter(template.getIncoming().getTimestampKey());

        HashMap<String, Object> payload = new HashMap<>();
        payload.put(SENDER, sender);
        payload.put(INBOUND_MESSAGE, message);
        payload.put(TIMESTAMP, timestamp);

        DateTime sentTime = timestamp == null ? null : DateTime.parse(timestamp);

        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.INBOUND_SMS, payload));

        smsAuditService.log(new SmsRecord(
                INBOUND, sender, message, sentTime, RECEIVED,
                Integer.toString(Math.abs(new Random().nextInt()))
        ));
    }
}
