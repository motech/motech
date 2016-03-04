package org.motechproject.admin.internal.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.bundles.BundleDirectoryManager;
import org.motechproject.admin.bundles.ExtendedBundleInformation;
import org.motechproject.admin.bundles.ImportExportResolver;
import org.motechproject.admin.bundles.MotechBundleFilter;
import org.motechproject.admin.exception.BundleNotFoundException;
import org.motechproject.admin.internal.service.ModuleAdminService;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.MotechEvent;
import org.motechproject.osgi.web.service.UIFrameworkService;
import org.motechproject.admin.bundles.BundleInformation;
import org.motechproject.server.config.domain.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.config.core.constants.ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT;

public class BundleAdminServiceTest {

    private static final long BUNDLE_ID = 1;

    @InjectMocks
    ModuleAdminService moduleAdminService = new ModuleAdminServiceImpl();

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    Dictionary<String, String> headers;

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

    @Mock
    MotechBundleFilter motechBundleFilter;

    @Mock
    CommonsMultipartResolver commonsMultipartResolver;

    @Mock
    MotechSettings motechSettings;

    @Mock
    ConfigurationService configurationService;

    @Mock
    UIFrameworkService uiFrameworkService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetBundles() {
        List<Bundle> bundleList = dummyBundleList();
        Bundle[] bundles = bundleList.toArray(new Bundle[bundleList.size()]);

        setupBundleRetrieval();
        when(bundleContext.getBundles()).thenReturn(bundles);
        when(motechBundleFilter.filter(bundles)).thenReturn(bundleList);

        assertEquals(toBundleInfoList(bundleList), moduleAdminService.getBundles());
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
    public void testUnInstallBundle() throws BundleException, IOException {
        setupBundleRetrieval();

        moduleAdminService.uninstallBundle(BUNDLE_ID, false);

        verify(bundleContext).getBundle(BUNDLE_ID);
        verify(bundle).uninstall();
        verify(bundleDirectoryManager).removeBundle(bundle);
    }

    @Test
    public void testGetBundleDetails() {
        final DateTime now = DateUtil.now();

        setupBundleRetrieval();
        when(bundle.getVersion()).thenReturn(version);
        when(bundle.getState()).thenReturn(Bundle.ACTIVE);
        when(bundle.getHeaders()).thenReturn(headers);
        when(bundle.getRegisteredServices()).thenReturn(new ServiceReference[]{ serviceReference });
        when(bundleContext.getService(serviceReference)).thenReturn(new Object());
        when(bundle.getServicesInUse()).thenReturn(new ServiceReference[]{ exposedServiceReference });
        when(bundleContext.getService(exposedServiceReference)).thenReturn(new Object());
        when(headers.get(ExtendedBundleInformation.BUILT_BY)).thenReturn("Builder");
        when(headers.get(ExtendedBundleInformation.BUILD_JDK)).thenReturn("JDK 7");
        when(headers.get(ExtendedBundleInformation.TOOL)).thenReturn("Hammer");
        when(headers.get(ExtendedBundleInformation.CREATED_BY)).thenReturn("Me");
        when(headers.get(ExtendedBundleInformation.VENDOR)).thenReturn("GF");
        when(headers.get(ExtendedBundleInformation.BUNDLE_ACTIVATOR)).thenReturn("org.my.Activator");
        when(headers.get(ExtendedBundleInformation.DESCRIPTION)).thenReturn("bla bla");
        when(headers.get(ExtendedBundleInformation.DOC_URL)).thenReturn("www.doc.org");
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn("imp1,imp2");
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn("exp1,exp2");
        when(headers.get(ExtendedBundleInformation.LAST_MODIFIED)).thenReturn(String.valueOf(now.getMillis()));

        ExtendedBundleInformation bundleInfo = moduleAdminService.getBundleDetails(BUNDLE_ID);

        verify(importExportResolver).resolveBundleWiring(bundleInfo);
        verify(bundleContext).getBundle(BUNDLE_ID);
        assertEquals(BundleInformation.State.ACTIVE, bundleInfo.getState());
        assertEquals(Arrays.asList(Object.class.getName()), bundleInfo.getRegisteredServices());
        assertEquals(Arrays.asList(Object.class.getName()), bundleInfo.getServicesInUse());
        assertEquals("Builder", bundleInfo.getBuiltBy());
        assertEquals("JDK 7", bundleInfo.getBuildJDK());
        assertEquals("Hammer", bundleInfo.getTool());
        assertEquals("Me", bundleInfo.getCreatedBy());
        assertEquals("GF", bundleInfo.getVendor());;
        assertEquals("org.my.Activator", bundleInfo.getBundleActivator());
        assertEquals("bla bla", bundleInfo.getDescription());
        assertEquals("www.doc.org", bundleInfo.getDocURL());
        assertEquals("imp1, imp2", bundleInfo.getImportPackageHeader());
        assertEquals("exp1, exp2", bundleInfo.getExportPackageHeader());
        assertEquals(now, bundleInfo.getLastModified());
    }

    @Test
    public void testSetUploadSize() {
        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);
        when(motechSettings.getUploadSize()).thenReturn("1000000");

        MotechEvent motechEvent = new MotechEvent(FILE_CHANGED_EVENT_SUBJECT);

        moduleAdminService.changeMaxUploadSize(motechEvent);

        verify(commonsMultipartResolver).setMaxUploadSize(Long.valueOf("1000000"));
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

    @Test(expected = BundleNotFoundException.class)
    public void testGet3rdPartyBundle() {
        setupBundleRetrieval();
        when(motechBundleFilter.passesCriteria(bundle)).thenReturn(false);

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
        when(bundle.getHeaders()).thenReturn(headers);
        when(motechBundleFilter.passesCriteria(any(Bundle.class))).thenReturn(true);
    }

    private static List<BundleInformation> toBundleInfoList(List<Bundle> bundles) {
        List<BundleInformation> bundleInfoList = new ArrayList<>();
        for (Bundle bundle : bundles) {
            bundleInfoList.add(new BundleInformation(bundle));
        }
        return bundleInfoList;
    }
}
