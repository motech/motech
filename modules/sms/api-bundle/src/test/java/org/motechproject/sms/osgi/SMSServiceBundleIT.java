package org.motechproject.sms.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.sms.api.constants.EventSubjects;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

public class SMSServiceBundleIT extends BaseOsgiIT {


    public void testSmsService() throws InterruptedException {

        final Object lock = new Object();
        final String subject = EventSubjects.SEND_SMS;
        final List<String> eventsReceived = new ArrayList<>();


        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));
        ServiceReference eventListenerRegistryRef = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        assertNotNull(eventListenerRegistryRef);


        EventListenerRegistry eventListenerRegistry = (EventListenerRegistry) bundleContext.getService(eventListenerRegistryRef);


        eventListenerRegistry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                eventsReceived.add(event.getSubject());
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public String getIdentifier() {
                return subject;
            }
        }, subject);


        assertNotNull(bundleContext.getServiceReference(MotechSchedulerService.class.getName()));

        ServiceReference smsServiceReference = bundleContext.getServiceReference(SmsService.class.getName());
        assertNotNull(smsServiceReference);

        SmsService smsService = (SmsService) bundleContext.getService(smsServiceReference);
        assertNotNull(smsService);


        smsService.sendSMS("1234", "Hi");


        new Wait(lock, 2000).start();

        assertEquals(1, eventsReceived.size());
        assertTrue(eventsReceived.contains(subject));
    }

    @Override
    protected List<String> getImports() {
        return new ArrayList<String>() {{
            //This package is not being imported by the bundle within which the test runs
            add("org.motechproject.event");
        }};
    }


}
