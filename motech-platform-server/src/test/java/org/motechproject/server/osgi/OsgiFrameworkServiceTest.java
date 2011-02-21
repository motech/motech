package org.motechproject.server.osgi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml" })
public class OsgiFrameworkServiceTest {

	@Autowired
	private OsgiFrameworkService service;
	
	@Autowired
	private Framework framework;

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void startStopTest() throws Exception {
		service.setApplicationContext(getWebApplicationContext());
		
		assertEquals(Bundle.INSTALLED, framework.getState());
		
		service.start();
		assertEquals(Bundle.ACTIVE, framework.getState());
		
		//expect framework bundle + fake bundle
		Bundle[] bundles = framework.getBundleContext().getBundles();
		assertEquals(2, bundles.length);
		for (Bundle bundle : bundles) {
			assertEquals(Bundle.ACTIVE, bundle.getState());
		}
		
		service.stop();
		assertEquals(Bundle.STOPPING, framework.getState());
	}

	private WebApplicationContext getWebApplicationContext() {
		GenericWebApplicationContext wac = (GenericWebApplicationContext) BeanUtils.instantiateClass(GenericWebApplicationContext.class);
		wac.setParent(applicationContext);
		wac.setServletContext(new MockServletContext());
		return wac;
	}
}
