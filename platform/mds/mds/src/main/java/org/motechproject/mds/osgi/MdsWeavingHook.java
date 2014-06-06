package org.motechproject.mds.osgi;

import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.javassist.MotechClassPool;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The MdsWeavingHook allows us to hook into the OSGi classloading process.
 * It replaces the DDE classes with their extended bytecode which we generated.
 * Moreover we dynamically add the required jdo imports.
 */
@Service
public class MdsWeavingHook implements WeavingHook {

    private static final Logger LOG = LoggerFactory.getLogger(MdsWeavingHook.class);

    @Override
    public void weave(WovenClass wovenClass) {
        String className = wovenClass.getClassName();

        // we should omit classes from slf4j library to avoid exception about missing class
        // definition of org.slf4j.helpers.MessageFormatter (it isn't exported by slf4j bundle)
        if (className.startsWith("org.slf4j")) {
            return;
        }

        LOG.trace("Weaving called for: {}", className);

        ClassData enhancedClassData = MotechClassPool.getEnhancedClassData(className);

        if (enhancedClassData == null) {
            LOG.trace("The class doesn't have enhanced metadata: {}", className);
        } else {
            LOG.info("Weaving {}", className);
            // these imports will be required by the provider
            addCommonImports(wovenClass);
            // change the bytecode
            wovenClass.setBytes(enhancedClassData.getBytecode());
        }
    }

    private void addCommonImports(WovenClass wovenClass) {
        List<String> dynamicImports = wovenClass.getDynamicImports();

        // jdo imports
        addIgnoreDuplicate(dynamicImports, "javax.jdo");
        addIgnoreDuplicate(dynamicImports, "javax.jdo.identity");
        addIgnoreDuplicate(dynamicImports, "javax.jdo.spi");

        // Joda time
        addIgnoreDuplicate(dynamicImports, "org.joda.time");

        // mds imports
        addIgnoreDuplicate(dynamicImports, "org.motechproject.mds.domain");
        addIgnoreDuplicate(dynamicImports, "org.motechproject.mds.filter");
        addIgnoreDuplicate(dynamicImports, "org.motechproject.mds.query");
        addIgnoreDuplicate(dynamicImports, "org.motechproject.mds.util");
    }

    private void addIgnoreDuplicate(List<String> imports, String item) {
        if (!imports.contains(item)) {
            imports.add(item);
        }
    }
}
