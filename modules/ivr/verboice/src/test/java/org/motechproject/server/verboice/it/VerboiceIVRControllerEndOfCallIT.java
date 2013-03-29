package org.motechproject.server.verboice.it;

import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.callflow.repository.AllFlowSessionRecords;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.EventKeys;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.server.verboice.web.VerboiceIVRController;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import static java.lang.String.format;
import static junit.framework.Assert.fail;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class VerboiceIVRControllerEndOfCallIT extends SpringIntegrationTest {

    public static final int EVENT_TIMEOUT = 12000;

    @Autowired
    VerboiceIVRController verboiceIVRController;

    @Autowired
    EventListenerRegistryService eventListenerRegistry;

    @Autowired
    FlowSessionService flowSessionService;

    @Autowired
    AllFlowSessionRecords allFlowSessionRecords;

    private MockMvc mockVerboiceIvrController;

    @Before
    public void setup() throws Exception {
        mockVerboiceIvrController = MockMvcBuilders.standaloneSetup(verboiceIVRController).build();
    }

    @Test
    public void shouldReceiveEndOfCallEventOnMissedCall() throws Exception {
        try {
            TestListener listener = new TestListener("end_of_call_test_missed_call_listener");
            eventListenerRegistry.registerListener(listener, EventKeys.END_OF_CALL_EVENT);
            String motechCallId = "mcid123";
            String phoneNumber = "12345";
            flowSessionService.findOrCreate(motechCallId, phoneNumber);
            String url = String.format("/ivr/callstatus?CallStatus=no-answer&CallSid=123A&motech_call_id=%s&From=%s", motechCallId, phoneNumber);
            mockVerboiceIvrController.perform(get(url));

            Object lock = listener.getLock();
            while (!listener.isEventReceived()) {
                synchronized (lock) {
                    lock.wait(EVENT_TIMEOUT);
                    if (!listener.isEventReceived())
                        fail(format("%s event not raised.", EventKeys.END_OF_CALL_EVENT));
                }
            }
        } finally {
            eventListenerRegistry.clearListenersForBean("end_of_call_test_hangup_listener");
        }
    }

    public static class TestListener implements EventListener {

        Object lock;
        boolean eventReceived;
        private String id;

        public TestListener(String id) {
            this.id = id;
            this.lock = new Object();
        }

        public Object getLock() {
            return lock;
        }

        public boolean isEventReceived() {
            return eventReceived;
        }

        @Override
        public void handle(MotechEvent event) {
            eventReceived = true;
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        @Override
        public String getIdentifier() {
            return id;
        }
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return null;
    }

    @After
    public void teardown() {
        allFlowSessionRecords.removeAll();
    }
}
