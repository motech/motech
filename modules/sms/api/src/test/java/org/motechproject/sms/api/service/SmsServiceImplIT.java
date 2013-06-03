package org.motechproject.sms.api.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.sms.api.constants.EventDataKeys;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.exceptions.SendSmsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testApplicationSmsApi.xml"})
public class SmsServiceImplIT {

    @Autowired
    SmsService smsService;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    @Test
    public void shouldRaiseSendSmsEvent() throws InterruptedException {
        try {
            TestEventListener listener = new TestEventListener("listener");
            eventListenerRegistry.registerListener(listener, EventSubjects.SEND_SMS);
            smsService.sendSMS(new SendSmsRequest(asList("123"), "hello", null));
            synchronized (listener.getReceivedEvents()) {
                listener.getReceivedEvents().wait(12000);
            }
            assertEquals(1, listener.getReceivedEvents().size());
            assertEquals(asList("123"), listener.getReceivedEvents().get(0).getParameters().get(EventDataKeys.RECIPIENTS));
            assertEquals("hello", listener.getReceivedEvents().get(0).getParameters().get(EventDataKeys.MESSAGE));
        } finally {
            eventListenerRegistry.clearListenersForBean("listener");
        }
    }

    @Test(expected = SendSmsException.class)
    public void shouldThrowExceptionForMissingRecipient() throws InterruptedException {
        try {
            TestEventListener listener = new TestEventListener("listener");
            eventListenerRegistry.registerListener(listener, EventSubjects.SEND_SMS);
            smsService.sendSMS(new SendSmsRequest(null, "hello"));
            synchronized (listener.getReceivedEvents()) {
                listener.getReceivedEvents().wait(2000);
            }
            assertEquals(0, listener.getReceivedEvents().size());
        } finally {
            eventListenerRegistry.clearListenersForBean("listener");
        }
    }

    class TestEventListener implements EventListener {

        private String id;
        private List<MotechEvent> receivedEvents = new ArrayList<>();

        public TestEventListener(String id) {
            this.id = id;
        }

        @Override
        public void handle(MotechEvent event) {
            synchronized (receivedEvents) {
                receivedEvents.add(event);
                receivedEvents.notifyAll();
            }
        }

        @Override
        public String getIdentifier() {
            return id;
        }

        public List<MotechEvent> getReceivedEvents() {
            return receivedEvents;
        }
    }
}
