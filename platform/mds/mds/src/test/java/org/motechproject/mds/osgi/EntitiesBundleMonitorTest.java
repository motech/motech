package org.motechproject.mds.osgi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.exception.MdsEntityWireException;
import org.motechproject.mds.exception.MdsException;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EntitiesBundleMonitorTest {

    @InjectMocks
    private EntitiesBundleMonitor monitor = new EntitiesBundleMonitor();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Bundle entitiesBundle;

    @Before
    public void setUp() {
        when(entitiesBundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
    }

    @Test(expected = MdsEntityWireException.class)
    public void shouldThrowEntityWireExceptionWhenUnableToResolveEntitiesBundle() throws BundleException {
        when(bundleContext.getBundles()).thenReturn(new Bundle[] { entitiesBundle });
        doThrow(new BundleException("Error resolving bundle", BundleException.RESOLVE_ERROR))
                .when(entitiesBundle).start();

        monitor.start();
    }

    @Test(expected = MdsException.class)
    public void shouldThrowEntityMdsExceptionForNonResolveErrors() throws BundleException {
        when(bundleContext.getBundles()).thenReturn(new Bundle[] { entitiesBundle });
        doThrow(new BundleException("Error resolving bundle", BundleException.MANIFEST_ERROR))
                .when(entitiesBundle).start();

        monitor.start();
    }
}
