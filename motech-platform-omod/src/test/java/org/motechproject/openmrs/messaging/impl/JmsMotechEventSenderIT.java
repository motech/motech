package org.motechproject.openmrs.messaging.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.motechproject.model.MotechEvent;
import org.motechproject.openmrs.messaging.MotechEventSender;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class JmsMotechEventSenderIT extends BaseModuleContextSensitiveTest {

	@Autowired
	private MotechEventSender motechEventSender;

	@Test
	public void testSend() throws Exception {
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("testparam", "testvalue");
		
		MotechEvent motechEvent = new MotechEvent("123", "test", parameters);
		
		motechEventSender.send(motechEvent);
		
		//TODO: add some assertions
	}
}
