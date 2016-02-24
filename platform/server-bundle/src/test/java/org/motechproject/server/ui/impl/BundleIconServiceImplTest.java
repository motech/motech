package org.motechproject.server.ui.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.ui.BundleIconService;
import org.motechproject.server.web.dto.BundleIcon;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Tests for the <code>BundleIconServiceImpl</code> class.
 */
public class BundleIconServiceImplTest {

    private static final long BUNDLE_ID = 1;
    private static final long BUNDLE_ID_2 = 2;
    private static final String BUNDLE_NAME = "Bundle";
    private static final String ICON_MIME = "image/png";
    private static final String DEFAULT_PATH = "/webapp/img/";
    private static final String DEFAULT_ICON = "bundle_icon.png";
    private static final String DEFAULT_ICON_2 = "iconTaskChannel.png";

    @InjectMocks
    BundleIconService bundleIconService = new BundleIconServiceImpl();

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldReturnIcon() throws IOException {
        setupBundleRetrieval();
        byte[] expectedIcon = readDefaultIcon(DEFAULT_PATH + DEFAULT_ICON);
        when(bundle.getResource("icon.gif")).thenReturn(getDefaultIconUrl(DEFAULT_PATH + DEFAULT_ICON));

        BundleIcon bundleIcon = bundleIconService.getBundleIconById(BUNDLE_ID, null);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(expectedIcon.length, bundleIcon.getContentLength());
        assertEquals(ICON_MIME, bundleIcon.getMime());
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).getResource("icon.gif");
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleDoesNotContainIcon() {
        setupBundleRetrieval();

        BundleIcon bundleIcon = bundleIconService.getBundleIconById(BUNDLE_ID, null);
        byte[] expectedIcon = readDefaultIcon(DEFAULT_PATH + DEFAULT_ICON);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(expectedIcon.length, bundleIcon.getContentLength());
        assertEquals(ICON_MIME, bundleIcon.getMime());

        for (String iconName : BundleIcon.ICON_LOCATIONS) {
            verify(bundle).getResource(iconName);
        }
    }

    @Test
    public void shouldReturnDefaultIconWhenBundleNotFound() {
        setupBundleRetrieval();

        BundleIcon bundleIcon = bundleIconService.getBundleIconById(BUNDLE_ID_2, null);
        byte[] expectedIcon = readDefaultIcon(DEFAULT_PATH + DEFAULT_ICON);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(ICON_MIME, bundleIcon.getMime());
    }

    @Test
    public void shouldReturnGivenDefaultIconWhenBundleDoesNotContainIcon() {
        setupBundleRetrieval();

        BundleIcon bundleIcon = bundleIconService.getBundleIconById(BUNDLE_ID, DEFAULT_ICON_2);
        byte[] expectedIcon = readDefaultIcon(DEFAULT_PATH + DEFAULT_ICON_2);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(ICON_MIME, bundleIcon.getMime());
    }

    @Test
    public void shouldReturnIconWhenGivenBundleName() {
        setupBundleRetrieval();
        when(bundle.getResource("icon.gif")).thenReturn(getDefaultIconUrl(DEFAULT_PATH + DEFAULT_ICON));

        BundleIcon bundleIcon = bundleIconService.getBundleIconByName(BUNDLE_NAME, null);
        byte[] expectedIcon = readDefaultIcon(DEFAULT_PATH + DEFAULT_ICON);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(ICON_MIME, bundleIcon.getMime());
    }

    private void setupBundleRetrieval() {
        when(bundleContext.getBundle(BUNDLE_ID)).thenReturn(bundle);
        when(bundleContext.getBundles()).thenReturn(new Bundle[] {bundle});
        when(bundle.getBundleId()).thenReturn(BUNDLE_ID);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_NAME);
    }

    private static byte[] readDefaultIcon(String path) {
        URL url = getDefaultIconUrl(path);
        try (InputStream is = url.openStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getDefaultIconUrl(String path) {
        return BundleIconServiceImplTest.class.getResource(path);
    }
}
