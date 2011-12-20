package org.motechproject.sms.smpp;

import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.SmsEventHandler;
import org.motechproject.sms.api.constants.EventSubject;
import org.motechproject.sms.api.service.SmsServiceImpl;

import java.util.List;

public class SmsSendHandler implements SmsEventHandler {

	private ManagedSmslibService service;

	public SmsSendHandler(ManagedSmslibService service) {
		this.service = service;
	}

	@Override
	@MotechListener(subjects = EventSubject.SEND_SMS)
	public void handle(MotechEvent event) throws Exception {
        List<String> recipients = ((List<String>) event.getParameters().get(SmsServiceImpl.RECIPIENTS));
		String text = (String) event.getParameters().get(SmsServiceImpl.MESSAGE);
		DateTime deliveryTime = (DateTime) event.getParameters().get(SmsServiceImpl.DELIVERY_TIME);

        if (deliveryTime == null)
    		service.queueMessage(recipients, text);
        else
            service.queueMessageAt(recipients, text, deliveryTime);
	}
}