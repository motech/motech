package org.motechproject.mds.listener;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;

import java.util.Dictionary;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FrameworkUtil.class)
public abstract class BaseListenerTest {

    @Mock
    private HistoryService historyService;

    @Mock
    private Bundle bundle;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceEvent serviceEvent;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Dictionary<String, String> headers;

    protected<T extends BaseListener> T setUpListener(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        mockStatic(FrameworkUtil.class);
        when(FrameworkUtil.getBundle((Class) any())).thenReturn(bundle);
        when(bundle.getBundleContext()).thenReturn(bundleContext);
        when(bundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        when(bundle.getHeaders()).thenReturn(headers);
        when(serviceReference.getBundle()).thenReturn(bundle);
        when(serviceEvent.getServiceReference()).thenReturn(serviceReference);
        when(serviceEvent.getType()).thenReturn(ServiceEvent.REGISTERED);
        when(bundleContext.getService(serviceReference)).thenReturn(applicationContext);

        T listener = clazz.newInstance();

        listener.serviceChanged(serviceEvent);

        return listener;
    }


    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
