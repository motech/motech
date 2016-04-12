package org.motechproject.server.osgi.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This weaving hook is in charge of making sure that the META-INF.services package is imported by bundles.
 * The reason for this is the introduction of support for javax.validation in Spring 3.2. Because of this each
 * module that used Spring MVC would have to contain/import a META-INF services file that would define the
 * validation provider. This hook was introduced in order to allow compatibility with old bundles
 */
public class ValidationWeavingHook implements WeavingHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationWeavingHook.class);

    private static final String SERVICES_FILE_PACKAGE = "META-INF.services";
    private static final String SERVICES_FILE = "META-INF/services/javax.validation.spi.ValidationProvider";
    private static final String FILTER = "filter";

    private static final String JSR_303_IMPORT = "org.apache.bval.jsr303";
    private static final String CONSTRAINTS_IMPORT = "org.apache.bval.constraints";

    @Override
    public void weave(WovenClass wovenClass) {
        BundleWiring wiring = wovenClass.getBundleWiring();

        if (!wovenClass.getDynamicImports().contains(SERVICES_FILE_PACKAGE) &&
                !hasRequirement(wiring, SERVICES_FILE_PACKAGE) && !hasFile(wiring, SERVICES_FILE)) {

            LOGGER.debug("Adding a {} dynamic import to {}", SERVICES_FILE_PACKAGE,
                    wiring.getBundle().getSymbolicName());

            wovenClass.getDynamicImports().add(SERVICES_FILE_PACKAGE);

            if (!wovenClass.getDynamicImports().contains(JSR_303_IMPORT)) {
                wovenClass.getDynamicImports().add(JSR_303_IMPORT);
            }
            if (!wovenClass.getDynamicImports().contains(CONSTRAINTS_IMPORT)) {
                wovenClass.getDynamicImports().add(CONSTRAINTS_IMPORT);
            }
        }
    }

    private boolean hasRequirement(BundleWiring wiring, String packageName) {
        List<BundleRequirement> reqs = wiring.getRequirements(null);
        for (BundleRequirement req : reqs) {
            String filter = req.getDirectives().get(FILTER);
            if (filter.contains(packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFile(BundleWiring wiring, String file) {
        Bundle bundle = wiring.getBundle();
        return bundle.getResource(file) != null;
    }
}
