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

@Component
public class ImportExportResolver {

    @Autowired
    private PackageAdmin packageAdmin;

    @Autowired
    private BundleContext bundleContext;

    public void resolveBundleWiring(ExtendedBundleInformation bundleInfo) {
        List<PackageInfo> imports = new ArrayList<>();
        List<PackageInfo> exports = new ArrayList<>();

        Bundle bundle = bundleContext.getBundle(bundleInfo.getBundleId());

        ExportedPackage[] allExportedPackages = packageAdmin.getExportedPackages((Bundle) null);

        for (ExportedPackage exportedPackage : allExportedPackages) {
            if (isImportedByBundle(exportedPackage, bundle)) {
                PackageInfo importInfo = new PackageInfo(exportedPackage) ;
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

    private boolean isImportedByBundle(ExportedPackage exportedPackage, Bundle bundle) {
        Bundle[] importingBundles = exportedPackage.getImportingBundles();
        return ArrayUtils.contains(importingBundles, bundle);
    }
}
