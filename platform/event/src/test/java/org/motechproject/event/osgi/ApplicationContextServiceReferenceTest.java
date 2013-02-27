package org.motechproject.event.osgi;

import org.eclipse.gemini.blueprint.mock.MockBundle;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationContextServiceReferenceTest {

    @Test
    public void shouldRecogniseAsApplicationContextService() {
        ServiceReference serviceReference = mock(ServiceReference.class);
        when(serviceReference.getBundle()).thenReturn(new MockBundle("mock-bundle"));
        when(serviceReference.getProperty(ApplicationContextServiceReference.SERVICE_NAME)).thenReturn("mock-bundle");


        when(serviceReference.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[]{"abcd", ApplicationContext.class.getName(), "xyz"});

        ApplicationContextServiceReference contextServiceReference = new ApplicationContextServiceReference(serviceReference);
        assertThat(contextServiceReference.isValid(), is(true));
    }

    @Test
    public void shouldNotRecogniseAsApplicationContextServiceWhenServiceNameIsDifferent() {
        ServiceReference serviceReference = mock(ServiceReference.class);
        when(serviceReference.getBundle()).thenReturn(new MockBundle("mock-bundle"));
        when(serviceReference.getProperty(ApplicationContextServiceReference.SERVICE_NAME)).thenReturn("another-mock-bundle");
        when(serviceReference.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[]{"abcd", ApplicationContext.class.getName(), "xyz"});

        ApplicationContextServiceReference contextServiceReference = new ApplicationContextServiceReference(serviceReference);
        assertThat(contextServiceReference.isValid(), is(false));
    }

    @Test
    public void shouldNotRecogniseAsApplicationContextServiceWhenObjectClassIsDifferent() {
        ServiceReference serviceReference = mock(ServiceReference.class);
        when(serviceReference.getBundle()).thenReturn(new MockBundle("mock-bundle"));
        when(serviceReference.getProperty(ApplicationContextServiceReference.SERVICE_NAME)).thenReturn("mock-bundle");
        when(serviceReference.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[]{"abcd", "he.llo", "xyz"});

        ApplicationContextServiceReference contextServiceReference = new ApplicationContextServiceReference(serviceReference);
        assertThat(contextServiceReference.isValid(), is(false));
    }

}
