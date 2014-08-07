package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.motechproject.osgi.web.util.BundleHeaders;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public final class MdsBundleHelper {

    public static boolean isBundleMdsDependent(Bundle bundle) {
        if (bundle == null) {
            return false;
        } else {
            BundleHeaders headers = new BundleHeaders(bundle);

            // check for Mds imports
            String imports = headers.getStringValue(org.osgi.framework.Constants.IMPORT_PACKAGE);
            if (StringUtils.contains(imports, Constants.Packages.BASE)) {
                return true;
            }

            // finally check for dynamic imports, if someone imports dynamically everything
            // we have to assume it can use MDS
            String dynamicImport = headers.getStringValue(org.osgi.framework.Constants.DYNAMICIMPORT_PACKAGE);
            return StringUtils.contains(dynamicImport, '*') ||
                    StringUtils.contains(dynamicImport, Constants.Packages.BASE);
        }
    }

    public static Bundle findMdsEntitiesBundle(BundleContext bundleContext) {
        return OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
                Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
    }

    public static Bundle findMdsBundle(BundleContext bundleContext) {
        return OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
                Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME);
    }

    public static boolean isMdsEntitiesBundle(Bundle bundle) {
        return Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME.equals(
                OsgiStringUtils.nullSafeSymbolicName(bundle));
    }

    public static boolean isMdsBundle(Bundle bundle) {
        return Constants.BundleNames.MDS_BUNDLE_SYMBOLIC_NAME.equals(
                OsgiStringUtils.nullSafeSymbolicName(bundle));
    }

    public static boolean isFrameworkBundle(Bundle bundle) {
        return bundle != null && bundle.getBundleId() == 0;
    }

    private MdsBundleHelper() {
    }
}
