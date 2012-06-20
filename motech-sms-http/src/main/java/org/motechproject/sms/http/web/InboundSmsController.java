package org.motechproject.sms.http.web;

import org.motechproject.scheduler.context.EventContext;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.http.TemplateReader;
import org.motechproject.sms.http.template.SmsHttpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/sms")
public class InboundSmsController {

    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();
    private SmsHttpTemplate template;

    @Autowired
    public InboundSmsController(TemplateReader templateReader) {
        this.template = templateReader.getTemplate();
    }

    public InboundSmsController(TemplateReader templateReader, EventRelay eventRelay) {
        this.template = templateReader.getTemplate();
        this.eventRelay = eventRelay;
    }

    @RequestMapping(value = "inbound")
    public void handle(HttpServletRequest request) {
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put(EventDataKeys.SENDER, request.getParameter(template.getIncoming().getSenderKey()));
        payload.put(EventDataKeys.INBOUND_MESSAGE, request.getParameter(template.getIncoming().getMessageKey()));
        payload.put(EventDataKeys.TIMESTAMP, request.getParameter(template.getIncoming().getTimestampKey()));
        eventRelay.sendEventMessage(new MotechEvent(EventSubjects.INBOUND_SMS, payload));
    }
}
