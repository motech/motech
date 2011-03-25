package org.motechproject.openmrs.messaging.impl;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.messaging.MotechEventSender;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class JmsMotechEventSenderIT extends BaseModuleContextSensitiveTest {

	@Autowired
	private MotechEventSender motechEventSender;
	
	@Autowired
	private QueueViewMBean queueMBean;

    @Before
    public void setup() throws Exception {
    	queueMBean.purge();
    }

    @After
    public void tearDown() throws Exception {
    	queueMBean.purge();
    }
	
	@Test
	public void testSend() throws Exception {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("testparam", "testvalue");
		
		MotechEvent motechEvent = new MotechEvent("123", "test", parameters);
		
		assertEquals(0, queueMBean.getQueueSize());

		motechEventSender.send(motechEvent);
		
		Thread.sleep(1000);
		assertEquals(1, queueMBean.getQueueSize());
	}
}
