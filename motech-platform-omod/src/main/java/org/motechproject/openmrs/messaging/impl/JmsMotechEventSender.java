package org.motechproject.openmrs.messaging.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.messaging.MotechEventSender;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsMotechEventSender implements MotechEventSender {

	private JmsTemplate jmsTemplate;

	@Override
	public void send(final MotechEvent motechEvent) {
		jmsTemplate.send(new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createObjectMessage(motechEvent);
			}
		});
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
}
