package org.motechproject.admin.bundles;

import org.apache.commons.lang.ArrayUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for resolving {@link Bundle} imports and exports. It fills up {@link ExtendedBundleInformation}
 * objects with additional information regarding {@link Bundle}'s imports and exports. This information gives
 * better insight into the current state of the OSGi framework.
 */
@Component
public class ImportExportResolver {

    @Autowired
    private PackageAdmin packageAdmin;

    @Autowired
    private BundleContext bundleContext;

    /**
     * Resolves the bundle wiring for the bundle represented by {@link ExtendedBundleInformation} by creating
     * {@link PackageInfo} objects. These objects represent an OSGi import containing information about both of the
     * involved parties - the importer and the exporter.
     *
     * @param bundleInfo the object representing the bundle, it will be filled with the import information. The
     *                   bundle is resolved based on the bundle ID returned by
     *                   {@link org.motechproject.admin.bundles.ExtendedBundleInformation#getBundleId()}
     * @see PackageInfo
     */
    public void resolveBundleWiring(ExtendedBundleInformation bundleInfo) {
        List<PackageInfo> imports = new ArrayList<>();
        List<PackageInfo> exports = new ArrayList<>();

        Bundle bundle = bundleContext.getBundle(bundleInfo.getBundleId());

        ExportedPackage[] allExportedPackages = packageAdmin.getExportedPackages((Bundle) null);

        for (ExportedPackage exportedPackage : allExportedPackages) {
            if (isImportedByBundle(exportedPackage, bundle)) {
                PackageInfo importInfo = new PackageInfo(exportedPackage);
                imports.add(importInfo);
            }

            if (exportedPackage.getExportingBundle().equals(bundle)) {
                PackageInfo exportInfo = new PackageInfo(exportedPackage);
                exports.add(exportInfo);
            }
        }

        bundleInfo.setBundleExports(exports);
        bundleInfo.setBundleImports(imports);
    }

    public void refreshPackage(Bundle bundle) {
        Bundle[] bundles = {bundle};
        packageAdmin.refreshPackages(bundles);
    }

    private boolean isImportedByBundle(ExportedPackage exportedPackage, Bundle bundle) {
        Bundle[] importingBundles = exportedPackage.getImportingBundles();
        return ArrayUtils.contains(importingBundles, bundle);
    }
}
