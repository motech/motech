package org.motechproject.context;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.event.EventListenerRegistry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class ContextTest {

	@Test
	public void testContext(){
		EventListenerRegistry eventListenerRegistry = Context.getInstance().getEventListenerRegistry();
		assertNotNull(eventListenerRegistry);
	}
}
