package org.motechproject.admin.bundles;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DefaultBundleFilterTest {

    private static final String MOTECH_IMPORT_EXPORT = "org.test;org.motechproject.api;org.xx";
    private static final String NON_MOTECH_IMPORT_EXPORT = "not.motech;ii.xx";
    private static final String NON_PLATFORM_NAME = "some-bundle";
    private static final String PLATFORM_NAME = "org.motechproject.motech-platform-bundle";

    @Mock
    private Bundle bundle;

    @Mock
    private Bundle adminBundle;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private Dictionary<Object, Object> headers;

    @InjectMocks
    private MotechBundleFilter bundleFilter = new DefaultBundleFilter();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(bundle.getHeaders()).thenReturn(headers);
        when(bundleContext.getBundle()).thenReturn(adminBundle);
        when(adminBundle.getSymbolicName()).thenReturn("org.motechproject.motech-admin-bundle");
    }

    @Test
    public void testWithMotechImport() {
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(NON_MOTECH_IMPORT_EXPORT);
        when(bundle.getSymbolicName()).thenReturn(NON_PLATFORM_NAME);

        assertTrue(bundleFilter.passesCriteria(bundle));
    }

    @Test
    public void testWithMotechExport() {
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn(NON_MOTECH_IMPORT_EXPORT);
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(bundle.getSymbolicName()).thenReturn(NON_PLATFORM_NAME);

        assertTrue(bundleFilter.passesCriteria(bundle));
    }

    @Test
    public void testPlatformBundle() {
        when(bundle.getSymbolicName()).thenReturn(PLATFORM_NAME);
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn(NON_MOTECH_IMPORT_EXPORT);

        assertFalse(bundleFilter.passesCriteria(bundle));
    }

    @Test
    public void testNonMotechBundle() {
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(NON_MOTECH_IMPORT_EXPORT);
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn(NON_MOTECH_IMPORT_EXPORT);
        when(bundle.getSymbolicName()).thenReturn(NON_PLATFORM_NAME);

        assertFalse(bundleFilter.passesCriteria(bundle));
    }

    @Test
    public void testAdminBundle() {
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(headers.get(ExtendedBundleInformation.IMPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(adminBundle.getHeaders()).thenReturn(headers);

        assertFalse(bundleFilter.passesCriteria(adminBundle));
    }

    @Test
    public void testFramework() {
        when(headers.get(ExtendedBundleInformation.EXPORT_PACKAGE)).thenReturn(MOTECH_IMPORT_EXPORT);
        when(bundle.getSymbolicName()).thenReturn("org.apache.felix.framework");

        assertFalse(bundleFilter.passesCriteria(bundle));
    }
}
