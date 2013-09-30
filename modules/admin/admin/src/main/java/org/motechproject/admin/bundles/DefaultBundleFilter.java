package org.motechproject.admin.bundles;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The default implementation of the {@link MotechBundleFilter}. It gives a pass only for non-platform
 * {@link Bundle}s that import or export the {@code org.motechproject} package.
 * To be counted as non-platform, the symbolic name of the {@link Bundle} mustn't start with
 * {@code "org.motechproject.motech-platform"}.
 * Because the system bundle({@code org.apache.felix.framework}) also imports/exports the {@code org.motechproject}
 * package, it is treated as an exception that will not pass the filter.
 */
@Component
public class DefaultBundleFilter extends MotechBundleFilter {

    private static final String MOTECH_PACKAGE = "org.motechproject";
    private static final String PLATFORM_PREFIX = MOTECH_PACKAGE + ".motech-platform";
    private static final String FRAMEWORK_NAME = "org.apache.felix.framework";

    @Autowired
    private BundleContext bundleContext;

    @Override
    public boolean passesCriteria(Bundle bundle) {
        return bundle != null && !isPlatformBundle(bundle) && importsExportsMotechPackage(bundle);
    }

    private boolean importsExportsMotechPackage(Bundle bundle) {
        String imports = (String) bundle.getHeaders().get(ExtendedBundleInformation.IMPORT_PACKAGE);
        String exports = (String) bundle.getHeaders().get(ExtendedBundleInformation.EXPORT_PACKAGE);

        return StringUtils.contains(imports, MOTECH_PACKAGE) || StringUtils.contains(exports, MOTECH_PACKAGE);
    }

    private boolean isPlatformBundle(Bundle bundle) {
        String symbolicName = bundle.getSymbolicName();
        return StringUtils.startsWith(symbolicName, PLATFORM_PREFIX) || StringUtils.equals(symbolicName, FRAMEWORK_NAME)
                || StringUtils.equals(symbolicName, bundleContext.getBundle().getSymbolicName());

    }
}
