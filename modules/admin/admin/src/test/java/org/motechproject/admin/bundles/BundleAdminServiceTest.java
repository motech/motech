package org.motechproject.admin.bundles;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.ex.BundleNotFoundException;
import org.motechproject.admin.service.ModuleAdminService;
import org.motechproject.admin.service.impl.ModuleAdminServiceImpl;
import org.motechproject.server.api.BundleIcon;
import org.motechproject.server.api.BundleInformation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BundleAdminServiceTest {

    private static final long BUNDLE_ID = 1;
    private static final String ICON_MIME = "image/png";
    private static final String BUNDLE_LOCATION = "C:\bundles";
    private static final String name = "Bundle name";

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

    @Mock
    ImportExportResolver importExportResolver;

    @Mock
    Version version;

    @Mock
    ServiceReference serviceReference;

    @Mock
    ServiceReference exposedServiceReference;

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

    @Test
    public void testGetBundleDetails() {
        setupBundleRetrieval();
        when(bundle.getVersion()).thenReturn(version);
        when(bundle.getState()).thenReturn(Bundle.ACTIVE);
        when(bundle.getHeaders()).thenReturn(headers);
        when(bundle.getRegisteredServices()).thenReturn(new ServiceReference[]{ serviceReference });
        when(bundleContext.getService(serviceReference)).thenReturn(new Object());
        when(bundle.getServicesInUse()).thenReturn(new ServiceReference[]{ exposedServiceReference });
        when(bundleContext.getService(exposedServiceReference)).thenReturn(new Object());
        when(headers.get(ExtendedBundleInformation.BUILD_JDK)).thenReturn("JDK 7");
        when(headers.get(ExtendedBundleInformation.TOOL)).thenReturn("Hammer");
        when(headers.get(ExtendedBundleInformation.CREATED_BY)).thenReturn("Me");
        when(headers.get(ExtendedBundleInformation.VENDOR)).thenReturn("GF");
        when(headers.get(ExtendedBundleInformation.BUNDLE_ACTIVATOR)).thenReturn("org.my.Activator");
        when(headers.get(ExtendedBundleInformation.DESCRIPTION)).thenReturn("bla bla");
        when(headers.get(ExtendedBundleInformation.DOC_URL)).thenReturn("www.doc.org");
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn("imp1,imp2");
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn("exp1,exp2");

        ExtendedBundleInformation bundleInfo = moduleAdminService.getBundleDetails(BUNDLE_ID);

        verify(importExportResolver).resolveBundleWiring(bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
        assertEquals(BundleInformation.State.ACTIVE, bundleInfo.getState());
        assertEquals(Arrays.asList(Object.class.getName()), bundleInfo.getRegisteredServices());
        assertEquals(Arrays.asList(Object.class.getName()), bundleInfo.getServicesInUse());
        assertEquals("JDK 7", bundleInfo.getBuildJDK());
        assertEquals("Hammer", bundleInfo.getTool());
        assertEquals("Me", bundleInfo.getCreatedBy());
        assertEquals("GF", bundleInfo.getVendor());;
        assertEquals("org.my.Activator", bundleInfo.getBundleActivator());
        assertEquals("bla bla", bundleInfo.getDescription());
        assertEquals("www.doc.org", bundleInfo.getDocURL());
        assertEquals("imp1, imp2", bundleInfo.getImportPackageHeader());
        assertEquals("exp1, exp2", bundleInfo.getExportPackageHeader());
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
        when(bundle.getBundleContext()).thenReturn(bundleContext);
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
