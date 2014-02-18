package org.motechproject.mds.osgi;

import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.javassist.MotechClassPool;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * The MdsWeavingHook allows us to hook into the OSGi classloading process.
 * It replaces the DDE classes with their extended bytecode which we generated.
 * Moreover we dynamically add the required jdo imports.
 */
@Service
public class MdsWeavingHook implements WeavingHook {

    private static final Logger LOG = LoggerFactory.getLogger(MdsWeavingHook.class);

    @PostConstruct
    public void init() {
        LOG.info("MDS weaving hook initialized");
    }

    @Override
    public void weave(WovenClass wovenClass) {
        String className = wovenClass.getClassName();

        LOG.trace("Weaving called for: {}", className);

        ClassData enhancedClassData = MotechClassPool.getEnhancedClassData(className);

        if (enhancedClassData == null) {
            LOG.trace("{} does not have enhanced registered DDE metadata", className);
        } else {
            LOG.debug("Weaving {}", className);
            // these imports will be required by the provider
            addCommonJdoImports(wovenClass);
            // change the bytecode
            wovenClass.setBytes(enhancedClassData.getBytecode());
        }
    }

    private void addCommonJdoImports(WovenClass wovenClass) {
        List<String> dynamicImports = wovenClass.getDynamicImports();

        dynamicImports.add("javax.jdo");
        dynamicImports.add("javax.jdo.spi");
    }
}
