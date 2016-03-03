package org.motechproject.bundle.extender;


import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.eclipse.gemini.blueprint.mock.MockBundleContext;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MotechOsgiApplicationContextCreatorTest {

    @Test
    public void shouldReturnApplicationOfTypeWebApplicationContext() throws Exception {
        MockBundle bundle = new MockBundle();
        MockBundleContext bundleContext = new MockBundleContext(bundle);
        ConfigurationScanner configurationScanner = mock(ConfigurationScanner.class);

        when(configurationScanner.getConfigurations(bundle)).thenReturn(new String[]{"resource.xml"});

        MotechOsgiApplicationContextCreator contextCreator = new MotechOsgiApplicationContextCreator();
        contextCreator.setConfigurationScanner(configurationScanner);

        MotechOsgiConfigurableApplicationContext applicationContext = (MotechOsgiConfigurableApplicationContext) contextCreator.createApplicationContext(bundleContext);
        assertThat(applicationContext, notNullValue());
    }

}
