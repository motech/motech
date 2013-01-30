package org.motechproject.admin.bundles;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultBundleFilter implements MotechBundleFilter {

    private static final String MOTECH_PACKAGE = "org.motechproject";
    private static final String PLATFORM_PREFIX = MOTECH_PACKAGE + ".motech-platform";
    private static final String FRAMEWORK_NAME = "org.apache.felix.framework";

    @Autowired
    private BundleContext bundleContext;

    @Override
    public List<Bundle> filter(Bundle[] bundles) {
        List<Bundle> result = new ArrayList<>();

        if (bundles != null) {
            for (Bundle bundle : bundles) {
                if (passesCriteria(bundle)) {
                    result.add(bundle);
                }
            }
        }

        return result;
    }

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
