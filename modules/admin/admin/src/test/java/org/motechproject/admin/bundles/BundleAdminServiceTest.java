package org.motechproject.admin.bundles;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.ex.BundleNotFoundException;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.admin.service.impl.ModuleAdminServiceImpl;
import org.motechproject.server.osgi.BundleInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class BundleAdminServiceTest {

    private static final long BUNDLE_ID = 1;
    private static final String ICON_MIME = "image/png";

    @InjectMocks
    ModuleAdminService moduleAdminService = new ModuleAdminServiceImpl();

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    Dictionary<Object, Object> headers;

    @Mock
    BundleDirectoryManager bundleDirectoryManager;

    @Mock
    File file;

    @Before
    public void setUp() {
        initMocks(this);
        when(bundle.getHeaders()).thenReturn(headers);
    }

    @Test
    public void testGetBundles() {
        List<Bundle> bundles = dummyBundleList();
        when(bundleContext.getBundles()).thenReturn(bundles.toArray(new Bundle[bundles.size()]));

        assertEquals(toBundleInfoList(bundles), moduleAdminService.getBundles());
        verify(bundleContext).getBundles();
    }

    @Test
    public void testGetBundleInfo() {
        setupBundleRetrieval();

        BundleInformation bundleInfo = moduleAdminService.getBundleInfo(BUNDLE_ID);

        assertEquals(new BundleInformation(bundle), bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
    }

    @Test
    public void testStartBundle() throws BundleException {
        setupBundleRetrieval();

        BundleInformation bundleInfo = moduleAdminService.startBundle(BUNDLE_ID);

        assertEquals(new BundleInformation(bundle), bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).start();
    }

    @Test
    public void testStopBundle() throws BundleException {
        setupBundleRetrieval();

        BundleInformation bundleInfo = moduleAdminService.stopBundle(BUNDLE_ID);

        assertEquals(new BundleInformation(bundle), bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).stop();
    }

    @Test
    public void testRestartBundle() throws BundleException {
        setupBundleRetrieval();

        BundleInformation bundleInfo = moduleAdminService.restartBundle(BUNDLE_ID);

        assertEquals(new BundleInformation(bundle), bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).stop();
        verify(bundle).start();
    }

    @Test
    public void testDefaultBundleIcon() {
        setupBundleRetrieval();

        BundleIcon bundleIcon = moduleAdminService.getBundleIcon(BUNDLE_ID);
        byte[] expectedIcon = readDefaultIcon();

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(expectedIcon.length, bundleIcon.getContentLength());
        assertEquals(ICON_MIME, bundleIcon.getMime());
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).getResource("icon.png");
        verify(bundle).getResource("icon.jpg");
        verify(bundle).getResource("icon.gif");
    }

    @Test
    public void testBundleIcon() throws IOException {
        setupBundleRetrieval();
        byte[] expectedIcon = readDefaultIcon();
        when(bundle.getResource("icon.gif")).thenReturn(getDefaultIconUrl());

        BundleIcon bundleIcon = moduleAdminService.getBundleIcon(BUNDLE_ID);

        assertArrayEquals(expectedIcon, bundleIcon.getIcon());
        assertEquals(expectedIcon.length, bundleIcon.getContentLength());
        assertEquals(ICON_MIME, bundleIcon.getMime());
        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).getResource("icon.gif");
    }

    @Test
    public void testUnInstallBundle() throws BundleException {
        setupBundleRetrieval();

        moduleAdminService.uninstallBundle(BUNDLE_ID);

        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).uninstall();
    }

    @Test(expected = BundleNotFoundException.class)
    public void testBundleStartNotFound() throws BundleException {
        moduleAdminService.startBundle(BUNDLE_ID);
    }

    @Test(expected = BundleNotFoundException.class)
    public void testBundleStopNotFound() throws BundleException {
        moduleAdminService.stopBundle(BUNDLE_ID);
    }

    @Test(expected = BundleNotFoundException.class)
    public void testBundleRestartNotFound() throws BundleException {
        moduleAdminService.restartBundle(BUNDLE_ID);
    }

    @Test(expected = BundleNotFoundException.class)
    public void testBundleInfoNotFound() {
        moduleAdminService.getBundleInfo(BUNDLE_ID);
    }

    private List<Bundle> dummyBundleList() {
        List<Bundle> bundles = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            bundles.add(bundle);
        }
        return bundles;
    }

    private void setupBundleRetrieval() {
        when(bundleContext.getBundle(BUNDLE_ID)).thenReturn(bundle);
        when(bundle.getBundleId()).thenReturn(BUNDLE_ID);
        when(bundle.getState()).thenReturn(Bundle.ACTIVE);
        when(bundle.getSymbolicName()).thenReturn("Bundle");
    }

    private static List<BundleInformation> toBundleInfoList(List<Bundle> bundles) {
        List<BundleInformation> bundleInfoList = new ArrayList<>();
        for (Bundle bundle : bundles) {
            bundleInfoList.add(new BundleInformation(bundle));
        }
        return bundleInfoList;
    }

    private static byte[] readDefaultIcon() {
        URL url = getDefaultIconUrl();
        try (InputStream is = url.openStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getDefaultIconUrl() {
        return BundleAdminServiceTest.class.getResource("/bundle_icon.png");
    }
}
