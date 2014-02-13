package org.motechproject.mds.testutil;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;

/**
 * This mock is required for ITs, so that the context is created properly.
 * It does not do anything.
 */
public class MockPackageAdmin implements PackageAdmin {
    @Override
    public ExportedPackage[] getExportedPackages(Bundle bundle) {
        return new ExportedPackage[0];
    }

    @Override
    public ExportedPackage[] getExportedPackages(String name) {
        return new ExportedPackage[0];
    }

    @Override
    public ExportedPackage getExportedPackage(String name) {
        return null;
    }

    @Override
    public void refreshPackages(Bundle[] bundles) {
    }

    @Override
    public boolean resolveBundles(Bundle[] bundles) {
        return false;
    }

    @Override
    public RequiredBundle[] getRequiredBundles(String symbolicName) {
        return new RequiredBundle[0];
    }

    @Override
    public Bundle[] getBundles(String symbolicName, String versionRange) {
        return new Bundle[0];
    }

    @Override
    public Bundle[] getFragments(Bundle bundle) {
        return new Bundle[0];
    }

    @Override
    public Bundle[] getHosts(Bundle bundle) {
        return new Bundle[0];
    }

    @Override
    public Bundle getBundle(Class clazz) {
        return null;
    }

    @Override
    public int getBundleType(Bundle bundle) {
        return 0;
    }
}
