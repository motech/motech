package org.motechproject.server.osgi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BundleTypeTest {

    @Mock
    private Bundle bundle;

    private Hashtable<String, String> headers = new Hashtable<>();

    @Before
    public void setUp() {
        when(bundle.getHeaders()).thenReturn(headers);
        headers.clear();
    }

    @Test
    public void shouldRecognizeFragmentBundles() {
        headers.put(Constants.FRAGMENT_HOST, "something");
        assertEquals(BundleType.FRAGMENT_BUNDLE, BundleType.forBundle(bundle));
    }

    @Test
    public void shouldRecognizeMdsBundle() {
        when(bundle.getSymbolicName()).thenReturn(PlatformConstants.MDS_BUNDLE_PREFIX);
        assertEquals(BundleType.MDS_BUNDLE, BundleType.forBundle(bundle));
    }

    @Test
    public void shouldRecognizeWsBundle() {
        when(bundle.getSymbolicName()).thenReturn(PlatformConstants.SECURITY_BUNDLE_SYMBOLIC_NAME);
        assertEquals(BundleType.WS_BUNDLE, BundleType.forBundle(bundle));
    }
}
