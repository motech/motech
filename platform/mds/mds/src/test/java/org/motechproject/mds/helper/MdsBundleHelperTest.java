package org.motechproject.mds.helper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.motechproject.mds.util.Constants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsBundleHelperTest {

    @Mock
    private Bundle entitiesBundle;

    @Mock
    private Bundle mdsBundle;

    @Mock
    private Bundle frameworkBundle;

    @Mock
    private Bundle nullSymNameBundle;

    @Mock
    private Bundle importsMdsBundle;

    @Mock
    private Bundle dynamicImportBundle;

    @Mock
    private Bundle nonMdsBundle;

    @Before
    public void setUp() {
        setUpBundleMock(frameworkBundle, "framework", 0);
        setUpBundleMock(entitiesBundle, Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME, 1);
        setUpBundleMock(mdsBundle, Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME, 2);
        setUpBundleMock(nullSymNameBundle, null, 3);
        setUpBundleMock(importsMdsBundle, "imports", 4, "org.something;org.motechproject.mds");
        setUpBundleMock(dynamicImportBundle, "dynImports", 5, "nonMds.package", "*");
        setUpBundleMock(nonMdsBundle, "nonMds", 6, "nonMds.package");

        when(bundleContext.getBundles()).thenReturn(new Bundle[] {nullSymNameBundle, frameworkBundle, entitiesBundle,
                mdsBundle, importsMdsBundle, dynamicImportBundle, nonMdsBundle});
    }

    @Mock
    private BundleContext bundleContext;

    @Test
    public void shouldReturnAndRecognizeEntitiesBundle() {
        assertEquals(entitiesBundle, MdsBundleHelper.findMdsEntitiesBundle(bundleContext));

        assertTrue(MdsBundleHelper.isMdsEntitiesBundle(entitiesBundle));

        assertFalse(MdsBundleHelper.isMdsEntitiesBundle(mdsBundle));
        assertFalse(MdsBundleHelper.isMdsEntitiesBundle(nullSymNameBundle));
        assertFalse(MdsBundleHelper.isMdsEntitiesBundle(importsMdsBundle));
    }

    @Test
    public void shouldReturnAndRecognizeMdsBundle() {
        assertEquals(mdsBundle, MdsBundleHelper.findMdsBundle(bundleContext));

        assertTrue(MdsBundleHelper.isMdsBundle(mdsBundle));

        assertFalse(MdsBundleHelper.isMdsBundle(entitiesBundle));
        assertFalse(MdsBundleHelper.isMdsBundle(nullSymNameBundle));
        assertFalse(MdsBundleHelper.isMdsBundle(importsMdsBundle));
    }

    @Test
    public void shouldRecognizeTheFrameworkBundle() {
        assertTrue(MdsBundleHelper.isFrameworkBundle(frameworkBundle));

        assertFalse(MdsBundleHelper.isFrameworkBundle(nullSymNameBundle));
        assertFalse(MdsBundleHelper.isFrameworkBundle(mdsBundle));
        assertFalse(MdsBundleHelper.isFrameworkBundle(importsMdsBundle));
    }

    @Test
    public void shouldRecognizeMdsDependentBundles() {
        assertTrue(MdsBundleHelper.isBundleMdsDependent(importsMdsBundle));
        assertTrue(MdsBundleHelper.isBundleMdsDependent(dynamicImportBundle));

        assertFalse(MdsBundleHelper.isBundleMdsDependent(frameworkBundle));
        assertFalse(MdsBundleHelper.isBundleMdsDependent(nullSymNameBundle));
        assertFalse(MdsBundleHelper.isBundleMdsDependent(nonMdsBundle));
    }

    private void setUpBundleMock(Bundle bundle, String symbolicName, long bundleId) {
        setUpBundleMock(bundle, symbolicName, bundleId, null, null);
    }

    private void setUpBundleMock(Bundle bundle, String symbolicName, long bundleId, String imports) {
        setUpBundleMock(bundle, symbolicName, bundleId, imports, null);
    }

    private void setUpBundleMock(Bundle bundle, String symbolicName, long bundleId, String imports, String dynamicImports) {
        when(bundle.getSymbolicName()).thenReturn(symbolicName);
        when(bundle.getBundleId()).thenReturn(bundleId);

        Dictionary<String, String> headers = new Hashtable<>();
        if (imports != null) {
            headers.put(org.osgi.framework.Constants.IMPORT_PACKAGE, imports);
        }
        if (dynamicImports != null) {
            headers.put(org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE, dynamicImports);
        }

        when(bundle.getHeaders()).thenReturn(headers);
    }
}
