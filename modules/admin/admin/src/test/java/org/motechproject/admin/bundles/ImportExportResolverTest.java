package org.motechproject.admin.bundles;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ImportExportResolverTest {

    @InjectMocks
    private ImportExportResolver importExportResolver = new ImportExportResolver();

    @Mock
    private PackageAdmin packageAdmin;

    @Mock
    private Bundle bundle;

    @Mock
    private Bundle otherBundle;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ExportedPackage exportedPackage;

    @Mock
    private ExportedPackage importedPackage;

    @Mock
    private ExportedPackage unrelatedPackage;

    @Mock
    private ExportedPackage secondImportedPackage;

    @Mock
    private Dictionary<Object, Object> headers;

    private final Version importedVersion = new Version(1, 1, 1);
    private final Version exportedVersion = new Version(2, 2, 2);
    private final Version secondImportedVersion = new Version(1, 2, 3);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testResolveImportExport() {
        ExportedPackage[] allExportedPackages = new ExportedPackage[] { exportedPackage, importedPackage, unrelatedPackage,
            secondImportedPackage };
        long bundleId = 1;

        when(bundle.getBundleId()).thenReturn(bundleId);
        when(bundle.getSymbolicName()).thenReturn("my.bundle");
        when(bundle.getHeaders()).thenReturn(headers);
        when(otherBundle.getSymbolicName()).thenReturn("other.bundle");
        when(bundleContext.getBundle(bundleId)).thenReturn(bundle);
        when(packageAdmin.getExportedPackages((Bundle) null)).thenReturn(allExportedPackages);

        setUpExport(exportedPackage, bundle, new Bundle[]{ otherBundle }, "my.export", exportedVersion);
        setUpExport(unrelatedPackage, otherBundle, new Bundle[]{ }, "unrelated", exportedVersion);
        setUpExport(importedPackage, otherBundle, new Bundle[]{ bundle }, "my.import", importedVersion);
        setUpExport(secondImportedPackage, otherBundle, new Bundle[]{ bundle }, "my.import.number.two", secondImportedVersion);

        ExtendedBundleInformation bundleInfo = new ExtendedBundleInformation(bundle);

        importExportResolver.resolveBundleWiring(bundleInfo);

        assertEquals(expectedExports(), bundleInfo.getBundleExports());
        assertEquals(expectedImports(), bundleInfo.getBundleImports());
    }

    private List<PackageInfo> expectedImports() {
        return Arrays.asList(new PackageInfo("my.import", "other.bundle", importedVersion.toString()),
                new PackageInfo("my.import.number.two", "other.bundle", secondImportedVersion.toString()));
    }

    private List<PackageInfo> expectedExports() {
        return Arrays.asList(new PackageInfo("my.export", "my.bundle", exportedVersion.toString()));
    }

    private void setUpExport(ExportedPackage exportedPackage, Bundle exportingBundle, Bundle[] importingBundles,
                             String name, Version version) {
        when(exportedPackage.getExportingBundle()).thenReturn(exportingBundle);
        when(exportedPackage.getName()).thenReturn(name);
        when(exportedPackage.getImportingBundles()).thenReturn(importingBundles);
        when(exportedPackage.getVersion()).thenReturn(version);
    }
}
